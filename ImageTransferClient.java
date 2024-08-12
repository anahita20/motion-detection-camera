
import java.net.Socket;
import java.io.InputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.util.Scanner;
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
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;


 
 class DecryptFile {

    KeyGenerator keyGenerator = null;
    SecretKey secretKey = null;
    Cipher cipher = null;

    public DecryptFile() {
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
  void decrypt(String srcPath, String destPath, SecretKey secretkey) {
        File encryptedFile = new File(srcPath);
        File decryptedFile = new File(destPath);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            /**
             * Initialize the cipher for decryption
             */
            cipher.init(Cipher.DECRYPT_MODE, secretkey);
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

public class ImageTransferClient {

	private String server;
	private int port;
	private String imagename;

	ImageTransferClient(String server, int port, String imagename) {
		this.server = server;
		this.port = port;
		this.imagename = imagename;
	}

	public void Transfer() {
		try {
			Socket s = new Socket(this.server, this.port);
			InputStream is = s.getInputStream();

			Scanner reader = new Scanner(is);
			
			System.out.print(reader.nextLine());
			int filesize = Integer.parseInt(reader.nextLine());
			String encodedKey= reader.nextLine();
			byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

			SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "Blowfish"); 
			DataInputStream dis = new DataInputStream(s.getInputStream());
		FileOutputStream fos = new FileOutputStream("encrypted_"+this.imagename);
			
			//BufferedImage bi = ImageIO.read(is);
byte[] buffer = new byte[4096];
		
		 // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
		
		fos.close();
		dis.close();

			s.close();

			//ImageIO.write(bi, "jpg", new File("encrypted_"+this.imagename));
			DecryptFile DecryptFile = new DecryptFile();
			

			DecryptFile.decrypt("encrypted_"+this.imagename,
                				"decrypted_"+this.imagename, secretKey);
			System.out.println("Decrypted file is decrypted_"+this.imagename);
		} catch(Exception e) { e.printStackTrace(); }
	}

	public static void main(String args[]) {
		ImageTransferClient itc = new ImageTransferClient("localhost", 12500, "temp2.jpg");
		itc.Transfer();
	}
}
