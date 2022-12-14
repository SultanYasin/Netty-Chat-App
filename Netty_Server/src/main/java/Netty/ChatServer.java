package Netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {

    /*
     * define bootstrap
     * in try/catch define the groups
     * ues tcp protokoll socket -> chennel(NioSocketChannel.class)
     * use initializer -> childHandler(new ChannelInitializer<SocketChannel>() -> inside of it define the pipeline
     * after all bind() with port
     * close the channels
     * */

    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public ChatServer(int port) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }


    public void start(){
        var bootstrap = new ServerBootstrap(); //bootstrap starta upp klient och server

        try{
            bootstrap.group(bossGroup , workerGroup)
                    .channel(NioServerSocketChannel.class)//use tcp
                    .childHandler(new ChannelInitializer<SocketChannel>() {// initializer hanterar anslutning mellan server och client
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
            /*pipeline har all info flöde*/ /*ChatServerHandler hanterar inkommande/utgående data + felhantering + bortkoppling*/
                            socketChannel.pipeline().addLast(new ChatServerHandler());
                        }
                    }).bind(port).sync().channel().closeFuture().sync();

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}
