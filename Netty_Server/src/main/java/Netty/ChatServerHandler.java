package Netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private  static final AttributeKey<String> USERNAME = AttributeKey.valueOf("username");
    //AttributeKey är saker som kopplas till channel för att lägga till information. Alltså vilken channel har vilken användare namn

    @Override  // hålla koll att kanalerna är aktiva
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
    }

    @Override //läsa av information from packet
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {

        var packetId = byteBuf.readByte();

        if (packetId==0) { //update name
            var name = readString(byteBuf);
            //koppla info till chanel
            System.out.println("update name to : " + name);
            ctx.channel().attr(USERNAME).set(name);

        } else if (packetId == 1 ){ // send message
            var message = readString(byteBuf);

            //send message to all in server with the name of the sender
            var name = ctx.channel().attr(USERNAME).get();

            for (var channel : channels){
                //helper klass för att skapa byteBuff
                var writeBuf = Unpooled.buffer();

                writeBuf.writeByte(0); // weather 0 to update name or 1 to send message

                writeBuf.writeInt(name.length());
                writeBuf.writeBytes(name.getBytes()); //convert the name form byte to string

                writeBuf.writeInt(message.length());
                writeBuf.writeBytes(message.getBytes());

                channel.writeAndFlush(writeBuf);
            }
        }

    }

    //convert buffer to string
    private static String readString(ByteBuf byteBuf){
        var length = byteBuf.readInt();
        var buffer = new byte[length];
        byteBuf.readBytes(buffer, 0, length);

        return new String(buffer, 0, length);
    }

}
