package code.me;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class ChatClient {

    private final String address;
    private final int port;
    private final EventLoopGroup eventLoops;

    public ChatClient(String address, int port) {
        this.eventLoops = new NioEventLoopGroup();
        this.address = address;
        this.port = port;
    }

    public void start(){
        var bootstrap = new Bootstrap();

        try{
            var channel = bootstrap.group(eventLoops)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addFirst(new ChatClientHandler());
                }
            })
              .connect(address , port ).sync().channel();
            var scanner = new Scanner(System.in);
            var line = scanner.nextLine();

            while (!line.equalsIgnoreCase("/exit")){

                if (line.toLowerCase().startsWith("/setname")){ //name changing

                    var name = line.substring(9); // length på /setname

                    var byteBuf = Unpooled.buffer();
                    byteBuf.writeByte(1); // packetID
                    byteBuf.writeInt(name.length());
                    byteBuf.writeBytes(name.getBytes());

                    channel.writeAndFlush(byteBuf);


                }else {
                    var byteBuf = Unpooled.buffer();
                    byteBuf.writeByte(1); // packetID
                    byteBuf.writeInt(line.length());
                    byteBuf.writeBytes(line.getBytes());

                    channel.writeAndFlush(byteBuf);
                }
                line = scanner.nextLine();
            }

            eventLoops.shutdownGracefully();

        }catch (Exception e ){
            System.out.println(e.getMessage());
        }

    }

}
