package com.Secret_Labs.secret_projectv10122.models;

public class Obj_ConvInfo {

    //Defining the data fields for the model
    private String conv_Id;
    private String convAcc_Id;
    private String convPartner_Id;
    private String convPartner_Username;
    private String convPartner_Quote;
    private byte[] convPartner_ProfilePic;
    private String convPartner_ProfilePicId;
    private String convLast_Message;
    private String convLast_MessageSender;
    private String convLast_MessageDate;


    public Obj_ConvInfo(String conv_Id, String convAcc_Id, String convPartner_Id, String convPartner_Username, String convPartner_Quote, byte[] convPartner_ProfilePic, String convPartner_ProfilePicId, String convLast_Message, String convLast_MessageSender, String convLast_MessageDate) {
        this.conv_Id = conv_Id;
        this.convAcc_Id = convAcc_Id;
        this.convPartner_Id = convPartner_Id;
        this.convPartner_Username = convPartner_Username;
        this.convPartner_Quote = convPartner_Quote;
        this.convPartner_ProfilePic = convPartner_ProfilePic;
        this.convPartner_ProfilePicId = convPartner_ProfilePicId;
        this.convLast_Message = convLast_Message;
        this.convLast_MessageSender = convLast_MessageSender;
        this.convLast_MessageDate = convLast_MessageDate;
    }

    public String getConv_Id() {
        return conv_Id;
    }

    public void setConv_Id(String conv_Id) {
        this.conv_Id = conv_Id;
    }

    public String getConvAcc_Id() {
        return convAcc_Id;
    }

    public void setConvAcc_Id(String convAcc_Id) {
        this.convAcc_Id = convAcc_Id;
    }

    public String getConvPartner_Id() {
        return convPartner_Id;
    }

    public void setConvPartner_Id(String convPartner_Id) {
        this.convPartner_Id = convPartner_Id;
    }

    public String getConvPartner_Username() {
        return convPartner_Username;
    }

    public void setConvPartner_Username(String convPartner_Username) {
        this.convPartner_Username = convPartner_Username;
    }

    public String getConvPartner_Quote() {
        return convPartner_Quote;
    }

    public void setConvPartner_Quote(String convPartner_Quote) {
        this.convPartner_Quote = convPartner_Quote;
    }

    public byte[] getConvPartner_ProfilePic() {
        return convPartner_ProfilePic;
    }

    public void setConvPartner_ProfilePic(byte[] convPartner_ProfilePic) {
        this.convPartner_ProfilePic = convPartner_ProfilePic;
    }

    public String getConvPartner_ProfilePicId() {
        return convPartner_ProfilePicId;
    }

    public void setConvPartner_ProfilePicId(String convPartner_ProfilePicId) {
        this.convPartner_ProfilePicId = convPartner_ProfilePicId;
    }

    public String getConvLast_Message() {
        return convLast_Message;
    }

    public void setConvLast_Message(String convLast_Message) {
        this.convLast_Message = convLast_Message;
    }

    public String getConvLast_MessageSender() {
        return convLast_MessageSender;
    }

    public void setConvLast_MessageSender(String convLast_MessageSender) {
        this.convLast_MessageSender = convLast_MessageSender;
    }

    public String getConvLast_MessageDate() {
        return convLast_MessageDate;
    }

    public void setConvLast_MessageDate(String convLast_MessageDate) {
        this.convLast_MessageDate = convLast_MessageDate;
    }
}
