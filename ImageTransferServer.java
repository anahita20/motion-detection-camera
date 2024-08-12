import java.net.ServerSocket;
import java.net.Socket;

import java.io.File;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.io.OutputStream;
import java.io.PrintWriter;
//package com.java.blowfish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


 class EncryptFile {

    KeyGenerator keyGenerator = null;
    SecretKey secretKey = null;
    Cipher cipher = null;

    public EncryptFile() {
        try {
            /**
             * Create a Blowfish key
             */
            keyGenerator = KeyGenerator.getInstance("Blowfish");
            secretKey = keyGenerator.generateKey();

            /**
             * Create an instance of cipher mentioning the name of algorithm
             *     - Blowfish
             */
            cipher = Cipher.getInstance("Blowfish");
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
        }
    }

    

    /**
     * 
     * @param srcPath
     * @param destPath
     *
     * Encrypts the file in srcPath and creates a file in destPath
     */
   String encrypt(String srcPath, String destPath) {
        File rawFile = new File(srcPath);
        File encryptedFile = new File(destPath);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            /**
             * Initialize the cipher for encryption
             */
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            /**
             * Initialize input and output streams
             */
            inStream = new FileInputStream(rawFile);
            outStream = new FileOutputStream(encryptedFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
	
String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		return encodedKey;
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
	return "";
        } catch (BadPaddingException ex) {
            System.out.println(ex);
	return "";
        } catch (InvalidKeyException ex) {
            System.out.println(ex);
return "";
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
return "";
        } catch (IOException ex) {
            System.out.println(ex);
return "";
        }
    }

    /**
     * 
     * @param srcPath
     * @param destPath
     *
     * Decrypts the file in srcPath and creates a file in destPath
     */
  void decrypt(String srcPath, String destPath) {
        File encryptedFile = new File(srcPath);
        File decryptedFile = new File(destPath);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            /**
             * Initialize the cipher for decryption
             */
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            /**
             * Initialize input and output streams
             */
            inStream = new FileInputStream(encryptedFile);
            outStream = new FileOutputStream(decryptedFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
        } catch (BadPaddingException ex) {
            System.out.println(ex);
        } catch (InvalidKeyException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}

public class ImageTransferServer {

	private String filename;
	private int port;

	public ImageTransferServer(String filename, int port) {
		this.filename = filename;
		this.port = port;
	}

	public void Transfer() {
		try {
			ServerSocket ss = new ServerSocket(this.port);
			Socket s = ss.accept();
			OutputStream os = s.getOutputStream();

			

			PrintWriter pw = new PrintWriter(os, true);
			pw.println("Image is ready!");
			EncryptFile encryptFile = new EncryptFile();
			

			String encodedKey=encryptFile.encrypt(this.filename,
                				"encrypted_"+this.filename);
			
			File myFile = new File ("encrypted_"+this.filename);
			pw.println(myFile.length());
			pw.println(encodedKey);
			//BufferedImage bi = ImageIO.read(new File("encrypted_"+this.filename));
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			FileInputStream fis = new FileInputStream("encrypted_"+this.filename);	
		
			byte[] buffer = new byte[4096];
		
			while (fis.read(buffer) > 0) {
				dos.write(buffer);
			}
			
			fis.close();
			dos.close();	
			//ImageIO.write(bi, "jpg", os);

			s.close();
			ss.close();
		} catch(Exception e) { e.printStackTrace(); }
	}

	public static void main(String args[]) {
		ImageTransferServer its = new ImageTransferServer("temp2.jpg", 12500);

		its.Transfer();
	}
}
