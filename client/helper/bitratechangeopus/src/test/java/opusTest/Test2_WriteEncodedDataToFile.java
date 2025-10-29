package opusTest;

import com.reve.audio.libraries.dlls.codec.opus.OPUSEncoder;
import com.reve.util.ShortArrayUtil;
import org.junit.jupiter.api.Test;

import java.io.*;

public class Test2_WriteEncodedDataToFile {
    @Test
    void applyFilters() throws IOException {
        int sampleRate = 48000;
        int bitRate = 24000;
        int packetDropPercentage = 5;
        int processingTimeMs = 10 * 2;
        int channel = 1;
        int maxByteArraySizeForEncoding = 2048;
        int chunkSizeShort = (sampleRate * processingTimeMs * channel) / 1000;
        String inputFilePath = "F:\\audio\\bitrate_control_loss_based_test\\send_packet\\input\\file_writing\\jakir_48khz_news.raw";
        String outputFilePath = "F:\\audio\\bitrate_control_loss_based_test\\send_packet\\input\\file_writing\\jakir_48khz_news_binary.bin";
        String csvFilePath = "F:\\audio\\bitrate_control_loss_based_test\\send_packet\\input\\file_writing\\jakir_48khz_news_csv.csv";

        OPUSEncoder opusEncoder = new OPUSEncoder(sampleRate, bitRate, processingTimeMs);
        opusEncoder.enableFEC(packetDropPercentage);

        File micFile = new File(inputFilePath);
        FileInputStream micFis = new FileInputStream(micFile);

        FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
        FileWriter csvWriter = new FileWriter(csvFilePath);

        byte[] micByte = new byte[chunkSizeShort * 2];
        int chunk_size = chunkSizeShort * 2;
        int micReadLength = 0;

        short[] encodeShortBuffer = new short[chunkSizeShort];
        byte[] tempByteArray = new byte[maxByteArraySizeForEncoding];

        int shortLen = 0;
        int encodeDataLength = 0;

        long t1 = System.currentTimeMillis();
        System.out.println("entering loop : "+t1);

        int i = 0;
        int index = 0;
        while(true){
            micReadLength = micFis.read(micByte, 0, chunk_size);

            if(micReadLength < chunk_size){
                System.out.println("breaking");
                break;
            }

            shortLen = ShortArrayUtil.convertByteToShort(micByte, 0, micReadLength, encodeShortBuffer, 0);
            encodeDataLength = opusEncoder.encode(encodeShortBuffer, 0, shortLen, tempByteArray, 0);

            byte[] encodeDataCopy = new byte[encodeDataLength];
            System.arraycopy(tempByteArray, 0, encodeDataCopy, 0, encodeDataLength);
            fos.write(encodeDataCopy);
            csvWriter.append(Integer.toString(encodeDataLength) + "\n");

            index += encodeDataLength;
            System.out.println(i + ": ShortLen: " + shortLen + " and EncodedDataLen: " + encodeDataLength + " index: " + index);
            i++;
        }

        long t2 = System.currentTimeMillis();
        System.out.println("time taken : "+(t2-t1)+" ms");
        micFis.close();
        fos.close();
        csvWriter.close();
        System.out.println("Length of output file is: " + index);
    }
}
