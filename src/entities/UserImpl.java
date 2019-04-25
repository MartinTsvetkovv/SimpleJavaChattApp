package entities;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class UserImpl {
    private InputStream inputStream;
    private PrintStream outputStream;
    private Socket socket;
    private String name;
    private int identifier;


    public UserImpl(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        this.inputStream = this.socket.getInputStream();
        this.outputStream = new PrintStream(this.socket.getOutputStream());
        this.identifier = UniqueIdentifier.getIdentifier();

    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public PrintStream getOutputStream() {
        return this.outputStream;
    }


    public String getName() {
        return this.name;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return this.name;

    }
}

