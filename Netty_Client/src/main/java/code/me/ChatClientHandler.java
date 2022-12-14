package code.me;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {

        var packetId = byteBuf.readByte();

        if (packetId == 0){ //receive msg
            var name = readString(byteBuf);
            var msg = readString(byteBuf);

            System.out.println(name + " : " +msg);
        }
    }


    public static String readString(ByteBuf byteBuf){

    var bufferLength = new byte[byteBuf.readInt()];
    byteBuf.readBytes(bufferLength);
    return new String(bufferLength);
    }

}
