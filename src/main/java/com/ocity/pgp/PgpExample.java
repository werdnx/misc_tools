package com.ocity.pgp;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.*;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

public class PgpExample {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // Load the public key ring from a file
        PGPPublicKey publicKey = readPublicKey(new FileInputStream("D:\\ocity\\saptco\\saab\\Sabb1@noexternalmail.hsbc.com_.pem"));

        // Encrypt the message
        String message = "H99999999999999920230207001\n" +
                "D23000044444444444433330000000000000050000000000050002302071721520010000000002302071721522412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071723110010000000002302071723112412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071733050010000000002302071733052412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071733190010000000002302071733192412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071734080010000000002302071734082412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071756120010000000002302071756122412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071818260010000000002302071818262412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071826560010000000002302071826562412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071828250010000000002302071828252412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "D23000044444444444433330000000000000050000000000050002302071842100010000000002302071842102412230208444444    000000000000000000000asda            3              NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN682682               VISA      41310000\n" +
                "I23020734000808682682000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
                "T000000000010";
        byte[] encryptedMessage = encryptMessage(message, publicKey);

        // Print the encrypted message
        System.out.println(new String(encryptedMessage));
    }

    private static byte[] encryptMessage(String message, PGPPublicKey encKey) throws Exception {
        ByteArrayOutputStream encOut = new ByteArrayOutputStream();

        OutputStream out = new ArmoredOutputStream(encOut);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                CompressionAlgorithmTags.ZIP);
        OutputStream cos = comData.open(bOut);

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        OutputStream pOut = lData.open(cos, PGPLiteralData.BINARY,
                PGPLiteralData.CONSOLE, message.getBytes().length, new Date());
        pOut.write(message.getBytes());

        lData.close();
        comData.close();

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_128).setWithIntegrityPacket(true).setSecureRandom(new SecureRandom()).setProvider("BC"));

        encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("BC"));

        byte[] bytes = bOut.toByteArray();
        OutputStream cOut = encGen.open(out, bytes.length);

        cOut.write(bytes);
        cOut.close();
        out.close();

        return encOut.toByteArray();
    }

    private static PGPPublicKey readPublicKey(InputStream input) throws IOException, PGPException {
        input = PGPUtil.getDecoderStream(input);

        JcaPGPPublicKeyRingCollection pgpPub = new JcaPGPPublicKeyRingCollection(input);

        //
        // we just loop through the collection till we find a key suitable for encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //
        PGPPublicKey key = null;

        //
        // iterate through the key rings.
        //
        Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();

        while (key == null && rIt.hasNext()) {
            PGPPublicKeyRing kRing = rIt.next();
            Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
            while (key == null && kIt.hasNext()) {
                PGPPublicKey k = kIt.next();

                if (k.isEncryptionKey()) {
                    key = k;
                }
            }
        }

        if (key == null) {
            throw new IllegalArgumentException("Can't find encryption key in key ring.");
        }

        return key;
    }

}
