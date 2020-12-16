package com.remote.remotecontrol.activity.ble;

public class SendCommand {
    public byte[] SendCommand(){
        byte [] demo_str = new byte[]{

        0x50, 0x6F, 0x6F, 0x68, //====> 4byte : systemID(i-Two Plus QS IR Blaster: (0x50, 0x6f, 0x6f, 0x68))
        0x00, 0x01,             //====> 2byte : RecordLength
        0x23,                   //====> 1byte : command code
        (byte)0x80, 0x00,       //====> 2byte : FrameCount

        //ZSF 1860.1 53byte
       (byte) 0x82, 0x01, 0x2F, 0x00, (byte) 0xA4, 0x73, 0x1C, (byte) 0x8B, (byte) 0xBD, (byte) 0x84, (byte) 0xCD, (byte) 0xA3, 0x29, 0x19, 0x48, (byte) 0xCC,
       0x69, (byte) 0xC2, 0x44, 0x23, 0x49, 0x1B, (byte) 0x98, 0x32, 0x08, 0x53, 0x04, (byte) 0xF7, (byte) 0xDF, 0x5D, (byte) 0xF7, (byte) 0xDF,
       0x7B, 0x75, (byte) 0xDF, 0x7B, (byte) 0x88, 0x02, (byte) 0x81, 0x40, 0x12, 0x32, 0x42, 0x55, 0x67, 0x26, 0x67, 0x28,
       0x42, 0x58, (byte) 0x90, 0x10, (byte) 0xD0,

        //ZSF 1860.1 43byte
      /*  (byte) 0x82, 0x01, 0x25, 0x00, (byte) 0xA4, 0x73, 0x10, 0x45, (byte) 0xA0, (byte) 0x91, (byte) 0x94, 0x69, (byte) 0xC2, 0x44, 0x23, 0x47,
        0x47, (byte) 0xD0, 0x0C, 0x4C, 0x09, 0x69, (byte) 0xA6, (byte) 0x9C, 0x71, (byte) 0xC7, 0x1C, 0x71, (byte) 0xA7, 0x1C, 0x72, 0x34,
        (byte) 0xD3, 0x00, 0x01, (byte) 0xC0, 0x4A, 0x72, 0x2B, (byte) 0xB6, (byte) 0xE0, 0x10, 0x40, //====> 43byte :삼성 on/off*/
        0x00, 0x00};           //====> 2byte : Checksum

        int demo_str_len = 11 + 53 ;
        demo_str[5] = 53+3+2;
        //demo_str[6] = UEI_SEND_IR_KEY;

        demo_str[demo_str_len-2] = demo_str[9];
        for(int cnt=0;cnt<52;cnt++)
        {
            demo_str[demo_str_len-2] ^= demo_str[10+cnt];
        }
        demo_str[demo_str_len-1] = (byte) ~demo_str[demo_str_len-2];

        return demo_str;
    }

    public byte[] IrSend(byte[] command, byte sequence){
        byte[] quickSet = new byte[20];
        int i = 2;

        quickSet[0] = (byte) 0xF0;  //Command
        quickSet[1] = sequence;  //Sequence
        switch (sequence){
            case 0x00:
                for(int j = 0 ; j<18 ; j++){
                    quickSet[i] = command[j];
                    i++;
                }
                break;
            case 0x01:
                for(int j = 18 ; j<36 ; j++){
                    quickSet[i] = command[j];
                    i++;
                }
                break;
            case 0x02:
                for(int j = 36 ; j<54 ; j++){
                    quickSet[i] = command[j];
                    i++;
                }
                break;
            case 0x03:
                if(command.length<=54){
                    quickSet = null;
                }else{
                    for(int j = 54 ; j<command.length ; j++){
                        quickSet[i] = command[j];
                        i++;
                    }
                }

                break;
        }
        return quickSet;
    }


}
