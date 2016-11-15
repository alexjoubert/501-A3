package main;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import objectGenerator.ObjGenerator;
import utilities.TextDisplay;
import utilities.General;

public class Driver {
	private List<String> mainMenuText;
	private List<String> serverConfig;
	private List<File> serialized;
	private Scanner input = new Scanner(System.in);
	private ObjGenerator objGen;
	private Serializer serializer;
	private String host;
	private int port;
	private String downloadDir = "downloaded";
	private Visualizer visualizer;

	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.mainMenu();
	}

	public Driver() {
		mainMenuText = General.readToList("src/main/assets/MainMenu.txt");
		serverConfig = General.readToList("src/main/assets/serverConfig.txt");
		objGen = new ObjGenerator();
		serializer = new Serializer();
		host = serverConfig.get(0);
		port = Integer.parseInt(serverConfig.get(1));
		visualizer = new Visualizer();
	}

	public void mainMenu() {
		switch (Driver.menuSelect(input, mainMenuText)) {
		case (1):
			objGen.objGeneratorMenu();
			break;
		case (2):
			TextDisplay.display("Generated objects: " + objGen.createdObjects().size());
			TextDisplay.display(objGen.createdObjects());
			TextDisplay.display(TextDisplay.repeatChar("#", 50));
			break;
		case (3):
			serialized = serializer.toFile(objGen.getObjList());
			TextDisplay.display("Output serialized documents to root");
			break;

		case (4):
			launchClient(host, port, serialized);
			break;
		case (5):
			launchServer(host, port, downloadDir);
			break;
		case (6):
			TextDisplay.display("Deserializing files...");
			List<File> downloadedFiles = General.getListOfFiles(downloadDir);
			List<Object> deserializedFiles = Deserializer.deserialize(Deserializer.fileToDocument(downloadedFiles));
			for (Object obj : deserializedFiles) {
				TextDisplay.display("Object Visualization: ");
				visualizer.visualize(obj, true);
				TextDisplay.display();
			}
			break;
		case (7):
			TextDisplay.display("Terminating program");
			System.exit(0);
		}
		mainMenu();
	}
	
	public static int getNextInt(Scanner userInput) {
		int input = -1;
		while (true) {
			try {
				input = userInput.nextInt();
			} catch (InputMismatchException e) {
				TextDisplay.display("Invalid selection");
				userInput.next();
			}
			return input;
		}
	}

	public static int menuSelect(Scanner userInput, List<String> menuList) {
		TextDisplay.display(menuList);
		int input = -1;
		do {
			TextDisplay.display("Please input a valid entry:");
			input = Driver.getNextInt(userInput);
		} while (input < 0 || input > menuList.size());
		TextDisplay.display("You have selected: " + input);
		TextDisplay.display(TextDisplay.repeatChar("#", 50));
		return input;
	}

	private static void launchServer(String host, int port, String dirPath) {
		TextDisplay.display("Accepting serialized files from: " + host + " and port: " + port);

		(new File(dirPath)).mkdirs(); 
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();

			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			DataInputStream dis = new DataInputStream(bis);

			int filesCount = dis.readInt();
			File[] files = new File[filesCount];
			TextDisplay.display("Number of files to be downloaded: " + filesCount);
			for (int i = 0; i < filesCount; i++) {
				long fileLength = dis.readLong();
				String fileName = dis.readUTF();

				files[i] = new File(dirPath + "/" + fileName);
				TextDisplay.display("Downloading file: " + fileName + " to " + dirPath);
				FileOutputStream fos = new FileOutputStream(files[i]);
				BufferedOutputStream bos = new BufferedOutputStream(fos);

				for (int j = 0; j < fileLength; j++)
					bos.write(bis.read());

				bos.close();
			}

			dis.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void launchClient(String host, int port, List<File> files) {
		TextDisplay.display("Sending serialized files to: " + host + " and port: " + port);

		Socket socket;
		try {
			socket = new Socket(InetAddress.getByName(host), port);

			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			DataOutputStream dos = new DataOutputStream(bos);

			dos.writeInt(files.size());

			for (File file : files) {
				long length = file.length();
				dos.writeLong(length);

				String name = file.getName();
				dos.writeUTF(name);

				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);

				int theByte = 0;
				while ((theByte = bis.read()) != -1)
					bos.write(theByte);

				bis.close();
			}

			dos.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			TextDisplay.display("Error: no server to connect to, prepare accepting side");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}