package net.shadow.utils;

import net.shadow.Shadow;

import java.security.MessageDigest;

public class HWID {

    /**
     * @return HWID in MD5;
     */

    public static String getHWID() {
        System.out.println(Shadow.c.getSession().getUsername().toLowerCase());
        if(Shadow.c.getSession().getUsername().toLowerCase().contains("zeonight")) {
            System.out.println("its zeonight eyyyy");
            return "ZEO-HWID";
        }
        try {
            String toEncrypt = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();

            byte byteData[] = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

}
