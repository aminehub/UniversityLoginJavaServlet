package HelpingUntilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

public class HashCodeMaker {
	
	
	public static String create_new_salt()
	{//generate the new salt byte representation
		SecureRandom srnd = new SecureRandom();
		byte[] raw_salt = new byte[16];
		srnd.nextBytes(raw_salt);
		String new_salt = to64(raw_salt);
		return new_salt;
	}

	
	public static String[] SHA512hashpass(String raw_pass, String new_salt)
	{//dhmiourgia hashed kwdikou
		String[] hash_and_salt = new String[2];
		byte[] raw_salt = null;
		raw_salt =from64(new_salt); 
		StringBuilder new_hash = null;
		try
		{
			new_hash = new StringBuilder();
			MessageDigest digester = MessageDigest.getInstance("SHA-512");
			digester.update(raw_salt);
			byte[] raw_hash = digester.digest(raw_pass.getBytes(StandardCharsets.UTF_8));
			for(int i=0; i<raw_hash.length; i++)
			{
				new_hash.append(Integer.toString((raw_hash[i] & 0xff) + 0x100, 16).substring(1));
			}

		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		hash_and_salt[0] = new_hash.toString();
		hash_and_salt[1] = new_salt;
		
		return hash_and_salt;
	}

	public static  boolean confirmpassword(String raw_pass, String hashed_pass,String new_salt)
	{
		String candidate_hash = SHA512hashpass(raw_pass,new_salt)[0];
	 	return candidate_hash.equals(hashed_pass);
	}
	
	private static String to64(byte[] raw_salt)
    {
        return DatatypeConverter.printBase64Binary(raw_salt);
    }

	private static byte[] from64(String new_salt)
	        throws IllegalArgumentException
	    {
	        return DatatypeConverter.parseBase64Binary(new_salt);
	    }
}
