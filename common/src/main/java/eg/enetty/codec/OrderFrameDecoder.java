package eg.enetty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    public OrderFrameDecoder(
        int maxFrameLength,
        int lengthFieldOffset,
        int lengthFieldLength,
        int lengthAdjustment,
        int initialBytesToStrip,
        boolean failFast
    ) {
        super(
            maxFrameLength,
            lengthFieldOffset,
            lengthFieldLength,
            lengthAdjustment,
            initialBytesToStrip,
            failFast
        );
    }

    public OrderFrameDecoder() {
        this(
            Integer.MAX_VALUE,
            0,
            2,
            0,
            2,
            true
        );
    }
}
