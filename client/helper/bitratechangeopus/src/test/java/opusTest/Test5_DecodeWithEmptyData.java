package opusTest;

import com.reve.audio.libraries.dlls.codec.opus.OPUSDecoder;
import com.reve.util.ShortArrayUtil;
import org.junit.jupiter.api.Test;

import java.io.*;

public class Test5_DecodeWithEmptyData {
    @Test
    void applyFilters() throws IOException {
        int sampleRate = 48000;
        int bitRate = 48000;
        int processingTimeMs = 10 * 2;
        int channel = 1;
        int chunkSizeShort = (sampleRate * processingTimeMs * channel) / 1000;

        String folder = "F:\\projects\\office_main_projects\\BitrateControlServer\\out\\artifacts\\BitrateControlServer_jar\\";

        String binaryFilePath = folder + "decoded_binary.bin";
        String csvFilePath = folder + "decoded_csv.csv";
        String decodedAudioFilePath = "F:\\audio\\bitrate_control_loss_based_test\\output\\no_loss_test\\input_48k_no_loss_startingBR_8kbps_7.raw";

        FileInputStream fis_binary = new FileInputStream(binaryFilePath);
        FileOutputStream fos_decoded_audio = new FileOutputStream(decodedAudioFilePath);
        byte[] encodedData = fis_binary.readAllBytes();
        System.out.println(encodedData.length);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath));
        String row;
        short[] encodeShortBuffer = new short[chunkSizeShort];
        byte[] tempByteArray = new byte[chunkSizeShort * 2];
        OPUSDecoder opusDecoder = new OPUSDecoder(sampleRate, bitRate, processingTimeMs);

        int sequenceNumber;
        int encodeBufferLen;

        byte[] emptyData = new byte[chunkSizeShort * 2];
        for (int i = 0; i < emptyData.length; i++) emptyData[i] = 0;

        boolean firstTime = true;
        int lastSequenceNumber = 0;

        int idx = 0;
        int index = 0;

        int count = 0;
        while ((row = bufferedReader.readLine()) != null) {

            if (idx == 0) {
                idx++;
                continue;
            }

            sequenceNumber = Integer.valueOf(row.split(",")[0]);
            encodeBufferLen = Integer.valueOf(row.split(",")[1]);

            if (firstTime) {
                lastSequenceNumber = sequenceNumber;
                firstTime = false;
            }

            if (sequenceNumber - lastSequenceNumber - 1 > 0) {
                System.out.println(sequenceNumber + " " + (sequenceNumber - lastSequenceNumber - 1));

//                if (sequenceNumber - lastSequenceNumber - 1 > 1)
                    count += (sequenceNumber - lastSequenceNumber - 1);
                for (int i = 0; i < sequenceNumber - lastSequenceNumber - 1; i++) {
                    System.out.println("------> hi");
                    fos_decoded_audio.write(emptyData, 0, emptyData.length);
                }
            }


            byte[] encodeDataByteCopy = new byte[encodeBufferLen];
            System.arraycopy(encodedData, index, encodeDataByteCopy, 0, encodeBufferLen);
            index += encodeBufferLen;

            int decodedDataLen = opusDecoder.decode(encodeDataByteCopy, 0, encodeBufferLen, encodeShortBuffer, 0);
            int byteLen = ShortArrayUtil.convertShortToByte(encodeShortBuffer, 0, decodedDataLen, tempByteArray, 0);
            fos_decoded_audio.write(tempByteArray, 0, byteLen);

            idx++;
            lastSequenceNumber = sequenceNumber;
        }
        fos_decoded_audio.close();

        System.out.println("Total lost frame: " + count);
    }
}
