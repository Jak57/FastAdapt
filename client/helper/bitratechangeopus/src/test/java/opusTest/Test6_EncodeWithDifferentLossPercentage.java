package opusTest;

import com.reve.audio.libraries.dlls.codec.opus.OPUSDecoder;
import com.reve.audio.libraries.dlls.codec.opus.OPUSEncoder;
import com.reve.util.ShortArrayUtil;
import org.junit.jupiter.api.Test;

import java.io.*;

public class Test6_EncodeWithDifferentLossPercentage {

    @Test
    void applyFilters() throws IOException {

        int sampleRate = 48000;

        int encoderBitRate = 48000;
        int decoderBitRate = 28000;

        int packetDropPercentage = 5;
        int processingTimeMs = 10 * 2;
        int channel = 1;
        int chunkSizeShort = (sampleRate * processingTimeMs * channel) / 1000;

        OPUSEncoder opusEncoder = new OPUSEncoder(sampleRate, encoderBitRate, processingTimeMs);
        opusEncoder.enableFEC(packetDropPercentage);
        OPUSDecoder opusDecoder = new OPUSDecoder(sampleRate, decoderBitRate, processingTimeMs);

        File micFile = new File("F:\\audio\\opus_bitrate_vs_loss_percentage_test\\input\\jakir_48khz.raw");
        FileInputStream micFis = new FileInputStream(micFile);

        String outputFolder = "F:\\audio\\opus_bitrate_vs_loss_percentage_test\\output\\audio\\";
        FileOutputStream fos = new FileOutputStream(new File(outputFolder + "jakir_48khz_lossPercentage_" + packetDropPercentage + ".raw"));

        String csvFilePath = "F:\\audio\\opus_bitrate_vs_loss_percentage_test\\output\\csv\\" + "jakir_48khzlossPercentage_" + packetDropPercentage + ".csv";
        FileWriter csvWriter = new FileWriter(csvFilePath);
        csvWriter.append("encoded_data_length\n");

        byte[] micByte = new byte[chunkSizeShort * 2];
        int chunk_size = chunkSizeShort * 2;
        int micReadLength = 0;

        short[] encodeShortBuffer = new short[chunkSizeShort];
        short[] decodedShortBuffer = new short[chunkSizeShort];

        byte[] tempByteArray = new byte[2048];


        int shortLen = 0;
        int byteLen = 0;
        int encodeDataLength = 0;
        int decodeDataLength = 0;

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

            //if (i < 100) {
            System.out.println(i + " encodedDataLength=" + encodeDataLength);
            //}
            decodeDataLength = opusDecoder.decode(tempByteArray, 0, encodeDataLength, decodedShortBuffer, 0);

            byteLen = ShortArrayUtil.convertShortToByte(decodedShortBuffer, 0, decodeDataLength, tempByteArray, 0);
            try {
                fos.write(tempByteArray, 0, byteLen);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;

        }

        long t2 = System.currentTimeMillis();
        System.out.println("time taken : "+(t2-t1)+" ms");
        micFis.close();
        fos.close();
        csvWriter.close();
    }
}
