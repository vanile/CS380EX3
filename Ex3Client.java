import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

public class Ex3Client {

	public static void main(String[] args) {
		try (Socket socket = new Socket("18.221.102.182", 38103)) {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			System.out.println("Connected to server.");

			int totalBytes = is.read();
			System.out.println("Reading " + totalBytes + " bytes.");

			byte[] bytes = new byte[totalBytes];

			System.out.println("Data received: ");

			printData(bytes, totalBytes, is);

			short check = checksum(bytes);
			System.out.print("\nChecksum calculated: ");
			System.out.printf("0x%02X", check);

			for (int i = 1; i >= 0; i--) {
				os.write(check >> (8 * i));
			}

			printResponse(is);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printResponse(InputStream is) throws IOException {
		int serverResponse = is.read();
		if (serverResponse == 1) {
			System.out.println("\nResponse good.");
		} else {
			System.out.println("\nResponse bad.");
		}
	}

	private static void printData(byte[] bytes, int totalBytes, InputStream is) throws IOException {
		byte byteValue;
		for (int i = 0; i < totalBytes; i++) {
			byteValue = (byte) is.read();
			bytes[i] = byteValue;

			if (i !=0 && i % 10 == 0) {
				System.out.println();
			}
			System.out.printf("%02X", bytes[i]);
		}

	}
	private static short checksum(byte[] bytes) {
		int sum = 0;
		int counter = 0;

		while (counter < bytes.length - 1) {
			byte firstByte = bytes[counter];
			byte secondByte = bytes[counter + 1];

			sum += ((firstByte << 8 & 0xFF00) | (secondByte & 0xFF));

			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
			counter += 2; //
		}

		if ((bytes.length) % 2 == 1) {
			byte last = bytes[bytes.length - 1];
			sum += ((last << 8) & 0xFF00);

			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
		}
		return (short) ~(sum & 0xFFFF);
	}
}