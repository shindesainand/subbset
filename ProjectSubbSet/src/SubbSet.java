import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.math.BigInteger;

public class SubbSet {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(args.length < 1)
		{
			System.out.println("This program requires a video file as an argument!");
			System.exit(1);
		}
		if(args.length > 1)
		{
			System.out.println("Whoa! Slow down there friend! I can take only one file at a time!");
			System.exit(1);
		}
		try
		{
			InetAddress.getByName("www.google.com");
		} 
		catch (IOException e) 
		{
			System.out.println("Please check your internet connection!");
			System.exit(1);
		}
		File movie = new File(args[0]);
		subSet(movie);
		System.exit(0);
	}
	
	public static void subSet(File filePath)
	{
		String extension = "";
		int i = 0;
		if(filePath.isFile())
			i = filePath.getName().lastIndexOf('.');
		if(i > 0)
			extension = filePath.getName().substring(i);
		else
			invalidFile();
		
		List<String> exts = Arrays.asList(".avi", ".mp4", ".mkv", ".mpg", ".mpeg", ".mov", ".rm", ".vob", ".wmv", ".flv", ".3gp");
		if(!exts.contains(extension))
			invalidFile();
		else
		{
			try
			{
				URL url = new URL("http://api.thesubdb.com/?action=download&hash="+getHash(filePath)+"&language=en");
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestProperty("User-Agent", "SubDB/1.0");
				BufferedReader br = new BufferedReader( new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer input = new StringBuffer("");
				while ((inputLine = br.readLine()) != null)
				{
					input.append(inputLine + "\n");
					//System.out.println(inputLine);
				}
				br.close();
				makeSub(filePath, input.toString());
			}
			catch(FileNotFoundException e)
			{
				System.out.println("Sorry, the requested subtitle was not found. :(");
			}
			catch(IOException ioe)
			{
				System.out.println("Oops! Something went wrong.");
			}
		}
	}
	
	public static void makeSub(File dest, String subs)
	{
		dest = new File(dest.getAbsolutePath()+".srt");
		try 
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));
			bw.write(subs);
			bw.close();
			dest.createNewFile();
			System.out.println("The requested sub-title has been downloaded! :)");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("There was a problem creating .srt file! :(");
		}
	}
	
	public static String getHash(File filePath)
	{
		String hexDigest = new String();
		try
		{
			RandomAccessFile file = new RandomAccessFile(filePath,"r");
			int readSize = 64 * 1024;
			byte data[] = new byte[2 * readSize];
			file.read(data);
			file.seek(file.length() - readSize);
			file.read(data, readSize, readSize);
			file.close();
//			System.out.println("Data length in bytes : "+ 2 * data.length);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data);
			BigInteger hash = new BigInteger(1, md5.digest());
	        hexDigest = hash.toString(16); // BigInteger strips leading 0's
	        while ( hexDigest.length() < 32 )
	        {
	        	hexDigest = "0" + hexDigest; // pad with leading 0's
	        }
		}
		catch(Exception e)
		{
			System.out.println("This program takes a VIDEO file as an argument! >:(");
			System.exit(1);
		}
		return hexDigest;
	}
	
	public static void invalidFile()
	{
		System.out.println("This program takes a VIDEO file as an argument! >:(");
		System.exit(1);
	}
}
