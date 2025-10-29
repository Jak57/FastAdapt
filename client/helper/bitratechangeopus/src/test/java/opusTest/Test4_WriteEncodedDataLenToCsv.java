package opusTest;

import com.reve.audio.libraries.dlls.codec.opus.OPUSEncoder;
import com.reve.util.ShortArrayUtil;
import org.junit.jupiter.api.Test;

import java.io.*;

public class Test4_WriteEncodedDataLenToCsv {

    @Test
    void applyFilters() throws IOException {

        int sampleRate = 48000;
        int bitRate = 48000;
        int packetDropPercentage = 5;
        int processingTimeMs = 10 * 2;
        int channel = 1;
        int chunkSizeShort = (sampleRate * processingTimeMs * channel) / 1000;

        OPUSEncoder opusEncoder = new OPUSEncoder(sampleRate, bitRate, processingTimeMs);
        opusEncoder.enableFEC(packetDropPercentage);

        File micFile = new File("F:\\audio\\bitrate_change_test\\input\\48khz\\record_48khz.raw");
        FileInputStream micFis = new FileInputStream(micFile);

        String outputFolder = "F:\\audio\\bitrate_change_test\\input\\48khz\\";
        String csvFilePath = outputFolder + "record_48khz_48kbps.csv";
        FileWriter csvWriter = new FileWriter(csvFilePath);


        byte[] micByte = new byte[chunkSizeShort * 2];
        int chunk_size = chunkSizeShort * 2;
        int micReadLength = 0;

        short[] encodeShortBuffer = new short[chunkSizeShort];
        byte[] tempByteArray = new byte[2048];

        int shortLen = 0;
        int encodeDataLength = 0;

        long t1 = System.currentTimeMillis();
        System.out.println("entering loop : "+t1);
        int i = 0;

        while(true){
            micReadLength = micFis.read(micByte, 0, chunk_size);

            if(micReadLength < chunk_size){
                System.out.println("breaking");
                break;
            }

            shortLen = ShortArrayUtil.convertByteToShort(micByte, 0, micReadLength, encodeShortBuffer, 0);
            encodeDataLength = opusEncoder.encode(encodeShortBuffer, 0, shortLen, tempByteArray, 0);
            csvWriter.append(Integer.toString(encodeDataLength) + "\n");
            System.out.println(i + " encodedDataLength=" + encodeDataLength);
            i++;

        }

        long t2 = System.currentTimeMillis();
        System.out.println("time taken : "+(t2-t1)+" ms");
        micFis.close();
        csvWriter.close();
    }
}
