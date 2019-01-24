package com.jrtclient.app;

import com.jrtclient.net.AuthHelper;
import com.jrtclient.net.NetClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {


    public Button connectButton;
    public Button localShell;
    public MenuButton selectShellMenu;
    public MenuItem bashMenuItem;
    public MenuItem shMenuItem;
    public MenuItem zshMenuItem;
    public TextField hostID;
    public TextField hostPassword;
    public TextField remoteID;
    public TextField remotePassword;

    public Circle connectDot;
    public Label connectLabel;

    private String selectedShell = "/bin/bash";

    private Channel channel;
    private StringProperty connectedLabelText = new SimpleStringProperty("Not Connected");
    private StringProperty hostIdProperty = new SimpleStringProperty();
    private IntegerProperty hostPasswordproperty = new SimpleIntegerProperty((int)(Math.random()*9000)+1000);
    private BooleanProperty remoteAuth = new SimpleBooleanProperty(false);

    public void initialize() {
        connectLabel.textProperty().bind(connectedLabelText);
        hostID.textProperty().bind(hostIdProperty);
        hostPassword.textProperty().bind(hostPasswordproperty.asString());

        bashMenuItem.setOnAction(event -> {
            selectShellMenu.setText(bashMenuItem.getText());
            selectedShell = "/bin/bash";
        });

        shMenuItem.setOnAction(event -> {
            selectShellMenu.setText(shMenuItem.getText());
            selectedShell = "cmd.exe";
        });
        zshMenuItem.setOnAction(event -> {
            selectShellMenu.setText(zshMenuItem.getText());
            selectedShell = "powershell.exe";
        });

        initializeConnection();
    }


    public void createSession() {
        /* TODO better textfield validation
                We need to validate inputs to prevent
                - connecting to itself
                - sending letters than numbers
        */
        if ((!remoteID.getText().isBlank()) && (!remotePassword.getText().isBlank())) {
            channel.writeAndFlush("connect|" + hostID.getText() + "|" + remoteID.getText() + "|" + remotePassword.getText());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TerminalWindow.fxml"));
                Stage terminalStage = new Stage();
                loader.setController(new RemoteShellController(selectedShell, channel));

                Parent terminalRoot = loader.load();

                Scene terminalScene = new Scene(terminalRoot);
                terminalStage.initModality(Modality.APPLICATION_MODAL);
                terminalStage.setScene(terminalScene);
                terminalStage.setTitle("Remote Terminal: " + remoteID.getText());

                terminalStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("xDDDD");
        }
    }

    public void createLocalSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TerminalWindow.fxml"));
            Stage terminalStage = new Stage();
            loader.setController(new LocalShellController(selectedShell));

            Parent terminalRoot = loader.load();

            Scene terminalScene = new Scene(terminalRoot);
            terminalStage.initModality(Modality.APPLICATION_MODAL);
            terminalStage.setScene(terminalScene);
            terminalStage.setTitle("Local Terminal");

            terminalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeConnection() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Task<Channel> clientTask = new Task<Channel>() {
            @Override
            protected Channel call() throws Exception {

                Bootstrap clientBoostrap = new Bootstrap();
                clientBoostrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline()
                                        .addLast(new StringDecoder())
                                        .addLast(new StringEncoder())
                                        .addLast("mainLogic", new NetClientHandler(hostIdProperty, remoteAuth));
                            }
                        });
                connectedLabelText.setValue("Connecting...");
                connectDot.setFill(Color.YELLOW);
                ChannelFuture f = clientBoostrap.connect("localhost", 2137).sync();
                return f.channel();
            }

            @Override
            protected void succeeded() {
                channel = getValue();
                connectedLabelText.setValue("Connected! :)");
                connectDot.setFill(Color.GREEN);
                AuthHelper.setPass(hostPasswordproperty.asString().get());
                AuthHelper.setId(hostIdProperty.get());
            }

            @Override
            protected void failed() {
                connectedLabelText.setValue("Connection failed :(");
                connectDot.setFill(Color.RED);

            }
        };

        new Thread(clientTask).start();
    }
}