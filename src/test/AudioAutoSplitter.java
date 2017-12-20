/**
 * 
 */
package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.converter.WaveFileObuffer;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.SampleBuffer;

/**
 * @author zhangle
 *
 */
public class AudioAutoSplitter {

    public static final int SIZE = 8747929;
    public static final float DURATION = 546.745f;

    public List<Integer> getSilenceList(String filePathName)
            throws IOException, UnsupportedAudioFileException, JavaLayerException {
        FileInputStream fis = new FileInputStream(filePathName);
        Bitstream bs = new Bitstream(fis);
        Decoder decoder = new Decoder();
        try {
            int i = 0;
            double minDb = Double.MAX_VALUE, maxDb = Double.MIN_VALUE;
            System.out.println("calculating...");
            int totalDuration = 0;
            List<Integer> silenceList = new ArrayList<Integer>();
            int duration = 0;
            double dbSum = 0;
            int frmCnt = 0;
            int minSingleDuration = 50000;
            int silenceDuration = 1000;
            int lastSilence = 0;

            while (true) {
                Header h = bs.readFrame();
                if (h == null) {
                    break;
                }

                // System.out.println("ms per frame:" + h.ms_per_frame());
                // System.out.println("framesize:" + h.framesize);
                // System.out.println("bitrate:" + h.bitrate());
                // System.out.println("frequency:" + h.frequency());
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bs);
                // System.out.println("buffer length:" +
                // output.getBufferLength());
                // System.out.println("channel count:" +
                // output.getChannelCount());
                // System.out.println("sample frequency:" +
                // output.getSampleFrequency());
                // System.out.println("----------------------------");
                short[] buffer = output.getBuffer();
                double db = this.calculateDb(buffer);
                double dbAvg = 0;
                dbSum += db;
                frmCnt++;
                duration += h.ms_per_frame();
                totalDuration += h.ms_per_frame();
                if (duration >= silenceDuration) {
                    dbAvg = dbSum / frmCnt;
                    if (dbAvg < minDb) {
                        minDb = db;
                    } else if (dbAvg > maxDb) {
                        maxDb = db;
                    }
                    if (dbAvg < 10) {
                        // silence detected
                        if (totalDuration - lastSilence > minSingleDuration) {
                            silenceList.add(totalDuration / 1000);
                            lastSilence = totalDuration;
                        }
                    }
                    frmCnt = 0;
                    dbSum = 0;
                    duration = 0;
                }
                bs.closeFrame();
                i++;
            }
            System.out.println("i:" + i + ", min:" + minDb + ", max:" + maxDb);
            System.out.println("silence count:" + silenceList.size());
            for (Integer s : silenceList) {
                System.out.print(s + ",");
            }
            System.out.println();
            return silenceList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ignore) {
                }
            }
            // if (as != null) {
            // try {
            // as.close();
            // } catch (Exception ignore) {
            // }
            // }
        }
    }

    public List<String> split(String srcFilePathName, String desFolder, List<Integer> silenceList) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(srcFilePathName);
        Bitstream bs = new Bitstream(fis);
        Decoder decoder = new Decoder();
        List<String> result = new ArrayList<String>();
        try {
            int totalDuration = 0;
            int silenceIdx = 0;
            Obuffer output = null;

            while (silenceIdx < silenceList.size()) {
                Header h = bs.readFrame();
                if (h == null) {
                    break;
                }

                if (output == null) {
                    decoder = new Decoder();
                    int channels = (h.mode() == Header.SINGLE_CHANNEL) ? 1 : 2;
                    int freq = h.frequency();
                    String destName = desFolder + "/_split_" + (silenceIdx + 1) + ".wav";
                    result.add(destName);
                    System.out.println("Saving file:" + destName);
                    output = new WaveFileObuffer(channels, freq, destName);
                    decoder.setOutputBuffer(output);
                }
                decoder.decodeFrame(h, bs);
                totalDuration += h.ms_per_frame();
                int silenceDuration = silenceList.get(silenceIdx) * 1000;
                if (totalDuration >= silenceDuration) {
                    output.set_stop_flag();
                    output.close();
                    output = null;
                    silenceIdx++;
                }
                bs.closeFrame();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ignore) {
                }
            }
            // if (as != null) {
            // try {
            // as.close();
            // } catch (Exception ignore) {
            // }
            // }
        }
        return result;
    }

    private double calculateDb(short[] raw) {
        int sum = 0;
        if (raw.length == 0) {
            return sum;
        } else {
            for (int ii = 0; ii < raw.length; ii++) {
                sum += Math.abs(raw[ii]);
            }
        }
        int average = sum / raw.length;
        if (average <= 0) {
            return 0;
        }
        double result = 20 * Math.log10(average);
        return result;
    }
    
    private String getFolderByFileName(String filePathName) {
        int pos = filePathName.lastIndexOf('/');
        if (pos < 0) {
            pos = filePathName.lastIndexOf('\\');
        }
        return filePathName.substring(0, pos);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        AudioAutoSplitter aas = new AudioAutoSplitter();
        try {
            if (args.length <= 0) {
                System.out.println("Usage: AudioAutoSplitter source_file_path_name");
                return;
            }
            String src = args[0];
            String folder = aas.getFolderByFileName(src);
            List<Integer> silenceList = aas.getSilenceList(src);
            List<String> wavList = aas.split(src, folder, silenceList);
            for (String wav : wavList) {
                String dst = wav.replace(".wav", ".mp3");
                AudioConverter ac = new AudioConverter(wav, dst);
                ac.convert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
