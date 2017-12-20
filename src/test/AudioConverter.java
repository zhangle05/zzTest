/**
 * 
 */
package test;

import java.io.File;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

/**
 * @author zhangle
 *
 */
public class AudioConverter {

    private final String src;
    private final String dst;

    public AudioConverter(String src, String dst) {
        this.src = src;
        this.dst = dst;
    }

    public void convert() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                System.out.println("Converting '" + AudioConverter.this.src + "'...");
                File source = new File(AudioConverter.this.src);
                File target = new File(AudioConverter.this.dst);
                AudioAttributes audio = new AudioAttributes();
                audio.setCodec("libmp3lame");
                audio.setBitRate(128000);
                audio.setChannels(2);
                audio.setSamplingRate(44100);
                EncodingAttributes attrs = new EncodingAttributes();
                attrs.setFormat("mp3");
                attrs.setAudioAttributes(audio);
                Encoder encoder = new Encoder();
                try {
                    encoder.encode(source, target, attrs);
                } catch (IllegalArgumentException | EncoderException e) {
                    e.printStackTrace();
                }
                System.out.println("Converting '" + AudioConverter.this.src + "' done!");
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "]:" + args[i]);
        }
        String src = "/Users/zhangle/Documents/work/xtech/tools/split_1.wav";
        String dst = "/Users/zhangle/Documents/work/xtech/tools/split_1.mp3";
        if (args.length > 0) {
            src = args[0];
        }
        if (args.length > 1) {
            dst = args[1];
        }
        AudioConverter ac = new AudioConverter(src, dst);
        try {
            ac.convert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
