package drawing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

//https://github.com/artclarke/humble-video/blob/master/humble-video-demos/src/main/java/io/humble/video/demos/RecordAndEncodeVideo.java
public class EncodeVideo {
    static int fps = 24;
    MediaPictureConverter converter = null;

    MediaPicture picture;
    Encoder encoder;
    final MediaPacket packet = MediaPacket.make();
    Muxer muxer;

    int width;
    int height;

    public EncodeVideo(String filename, String formatname, int width1, int height1) {
        width = width1;
        height = height1;
        try {
            build(filename, formatname);
        } catch (Exception ignored) {
        }
    }


    private void build(String filename, String formatname) throws InterruptedException, IOException {
        final Rectangle screenbounds = new Rectangle(0, 0, width, height);

        final Rational framerate = Rational.make(1, fps);

        muxer = Muxer.make(filename, null, formatname);

        final MuxerFormat format = muxer.getFormat();
        final Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        encoder = Encoder.make(codec);
        encoder.setWidth(screenbounds.width);
        encoder.setHeight(screenbounds.height);
        // We are going to use 420P as the format because that's what most video formats these days use
        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate);

        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);
        picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
        picture.setTimeBase(framerate);
    }

    int ts = 0;

    public void encodeFrame(BufferedImage image) {
        /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
        if (converter == null)
            converter = MediaPictureConverterFactory.createConverter(image, picture);

        converter.toPicture(picture, image, ts++);
        do {
            encoder.encode(packet, picture);
            if (packet.isComplete())
                muxer.write(packet, false);
        } while (packet.isComplete());
    }

    public void close() {
        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet, false);
        } while (packet.isComplete());
        muxer.close();
    }
}