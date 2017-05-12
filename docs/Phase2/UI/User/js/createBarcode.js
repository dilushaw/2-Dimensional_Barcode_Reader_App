// JavaScript Document
    var barcodeNumber=0;
var barcodeIDs=[];
var barcodeLengths=[];
var barcodeWidths=[];


function Barcode_action(){
//alert("test");

    var act=$('#BCaction').val();

    $('#BC1').hide();
    $('#BC2').hide();
    $('#BC3').hide();
    $('#BC4').hide();
    $('#BC5').hide();
    $('#ACT1').hide();
    $('#ACT2').hide();
    $('#ACT3').hide();
    $('#ACT4').hide();
    $('#ACT5').hide();


    switch(act){
        case "1": $('#BC1').show();$('#ACT1').show();break;
        case "2": $('#BC2').show();$('#ACT2').show();break;
        case "3": $('#BC3').show();$('#ACT3').show();break;
        case "4": $('#BC4').show();$('#ACT4').show();break;
        case "5": $('#BC5').show();$('#ACT5').show();break;

    }

    $("#websiteAddress").val('');
    $("#message").val('');
    $("#contactName").val('');
    $("#contactPhone").val('');
    $("#contactEmail").val('');
    $("#sms").val('');
    $("#smsmessage").val('');
    $("#call").val('');

}

function changeBarcodeTypeLogged(){

    var barCodeType;
    barCodeType=$('input:radio[name=barCodetype]:checked').val();

    if(barCodeType=="2"||privilegeArray[1]=="0")
        $("#errorCorrection").attr('disabled', 'disabled');
    else
        $("#errorCorrection").removeAttr('disabled');

}

function changeLength(){

    var barcodeWidth=$("#barcodeWidth option:selected").val();
    $("#barcodeLength").val(barcodeWidth);

}

function changeWidth(){

    var barcodeLength=$("#barcodeLength option:selected").val();
    $("#barcodeWidth").val(barcodeLength);

}

function addMultipleBarcodes(){

    var size= $("#barcodeLength option:selected").val();

    $("#barcodeTable > tbody").append("<tr id='tableRow"+barcodeNumber+"'><td>"+size+" x "+size+" <a href='#' onclick=\'deleteBarcodeRow("+barcodeNumber+")\'>Remove</a>"+"</td></tr>");
   
    barcodeIDs[barcodeNumber]=barcodeNumber;
    barcodeLengths[barcodeNumber]=size;
    barcodeWidths[barcodeNumber]=size;
    barcodeNumber++;

    createBarcodeImages(barcodeNumber,size,size);
    

}

function deleteBarcodeRow(barcodeNumber){

    $('#tableRow' + barcodeNumber).remove();
    removeBarcodeImages(barcodeNumber);
    deleteBarcode(barcodeNumber);
    

}

function deleteBarcode(barcodeNumber){

    barcodeLengths[barcodeNumber]=-1;
    barcodeWidths[barcodeNumber]=-1;

}

function createBarcodeImages(barcodeNumber,barcodeLength,barcodeWidth){

    var size= barcodeLength*39;/*The size of a barcode to be displayed*/
    $("#imageTable>tbody").append("<tr id='imageRow"+barcodeNumber+"'><td><img src='images/NoImage.png' height='"+size+"' /></td></tr>");

}

function removeBarcodeImages(barcodeNumber){

    var barcodeNumberReduced=barcodeNumber+1;
    $('#imageRow'+barcodeNumberReduced).remove();

}

function startOverLogged(){
$('#BC1').show();
    $('#BC2').hide();
    $('#BC3').hide();
    $('#BC4').hide();
    $('#BC5').hide();
    $('#ACT1').show();
    $('#ACT2').hide();
    $('#ACT3').hide();
    $('#ACT4').hide();
    $('#ACT5').hide();

    $("#BCaction").val(1);
    $("#websiteAddress").val("");
    $("#message").val('');
    $("#contactName").val('');
    $("#contactPhone").val('');
    $("#contactEmail").val('');
    $("#sms").val('');
    $("#call").val('');
    $("#titleText").val('');
    $("#smsmessage").val('');
    $("#barcodeLength").val(1);
    $("#barcodeWidth").val(1);
    $("#errorCorrection").val(1);
    $("#imageType").val(1);
    $("#typeQR").prop('checked', false);
    $("#typeDM").prop('checked', false);
    $("#errorCorrection").removeAttr('disabled');


    $("#msgboxcount").val('2000');
    $("#smsmsgboxcount").val('160');


    $("#getZip").attr('disabled', 'disabled');

    $("[name=barCodetype]").filter("[value=1]").attr("checked","checked");

    $("#imageTable tr").remove();
    $("#imageTable > tbody").append("<tr id=\"imageLine1\"></tr>");



    for(var i=0;i<barcodeNumber;i++){

        deleteBarcodeRow(i);
    
    }

    
}
