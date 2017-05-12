    <script type="text/javascript">

    var barcodeNumber=0;
var barcodeIDs=[];
var barcodeLengths=[];
var barcodeWidths=[];
var privilegeArray;

$(document).ready(function() {

    var privileges;
    
    startOverLogged();
    $("[name=barCodetype]").filter("[value=1]").attr("checked","checked");

    privilegeArray = new Array();

    var user=$("#userNameVal").val();
 
    $.ajax({

        url: 'checkUserPrivileges.htm',
        async:false,
        data: ({userName:user}),
        success: function(data) {
        
            privileges=data;
            
        }

    });
    privilegeArray=privileges.split(',');
    

    if(privilegeArray[0]=="0"){
        $("#imageType").attr('disabled', 'disabled');
    }
    if(privilegeArray[1]=="0"){
        $("#errorCorrection").attr('disabled', 'disabled');
    }
    if(privilegeArray[2]=="0"){
        $("#addSize").attr('disabled', 'disabled');
        $("#barcodeLength").attr('disabled', 'disabled');
        $("#barcodeWidth").attr('disabled', 'disabled');
    }
    if(privilegeArray[3]=="0"){
        $("#file1").attr('disabled', 'disabled');
        $("#generateCodes").attr('disabled', 'disabled');
    }
    if(privilegeArray[4]=="0"){
        $("#getZip").attr('disabled', 'disabled');
    }
    

});



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


function validate(barcodeAction,webSiteAddress,message,contactName,contactPhone,contactEmail,sms,call,smsmessage){
    var msg;

    switch(barcodeAction){

        case "1":

            regex = /^(((http|ftp|https){1}(:[/][/]){1})|((www.){1}))[-a-zA-Z0-9@:%_\+.~#?&//=]+$/;
            if(webSiteAddress==""){
                msg= "Please insert a website address!!";
            }
            else if(regex.test(webSiteAddress) == false){
                msg="Website URL is not valid .";
            }
            break;
        case "2":
            if(message==""){
                msg= "Please insert a message!!";
            }

            break;
        case "3":
            regex = /^((\d+)|(\+(\d+)))$/;
            if(contactName==""){
                msg= "Please insert a Contact Name!!";
            }
            else if(contactPhone==""){
                msg= "Please insert a Phone Number!!";
            }
            else if(contactEmail==""){
                msg= "Please insert a email!!";
            }

            else if(!contactPhone.match(regex) ){
                msg ="The phone number is incorrect";

            }
            else {

                apos=contactEmail.indexOf("@");
                dotpos=contactEmail.lastIndexOf(".");
                if (apos<1||dotpos-apos<2){
                    msg ="The email address is incorrect";

                }

            }
            break;
        case "4":
            regex = /^\d{9}$/;
            if(sms=="77"){
                msg= "Please insert the number!!";
            }
            else if( !sms.match(regex) ){
                msg= "The number is incorrect.";

            }
            break;

        case "5":
            regex = /^\d{9}$/;
            if(call=="77"){
                msg= "Please insert the number!!";
            }
            else if( !call.match(regex) ){
                msg= "The number is incorrect.";

            }
            break;


    }

    return msg;




}



function submitBarcodeParamsLogged(userName,userAccessLevel,ipAddress){

    
    $("#getZip").attr('disabled', 'disabled');

    var barcodeAction;
    var webSiteAddress;
    var message;
    var contactName;
    var contactPhone;
    var contactEmail;
    var sms;
    var call;
    var barcodeType;
    var imageType;
    var errorCorrection;
    var title;
    var smsmessage;

    barcodeAction=$("#bcAction").val();
    webSiteAddress=$("#websiteAddress").val();
    message=$("#message").val();
    contactName=$("#contactName").val();
    contactPhone=$("#contactPhone").val();
    contactEmail=$("#contactEmail").val();
    sms="77"+$("#sms").val();
    call="77"+$("#call").val();
    barcodeType=$('input:radio[name=barCodetype]:checked').val();
    imageType=$("#imageType").val();
    errorCorrection=$("#errorCorrection").val();
    title=$("#titleText").val();
    smsmessage=$("#smsmessage").val();


    msg =validate(barcodeAction,webSiteAddress,message,contactName,contactPhone,contactEmail,sms,call,smsmessage);
    if (msg) {
	$("#response").html(msg);
        return;
    }

   


     var bclength=barcodeLengths.toString();
    if ((bclength=="-1")||(bclength=="")){
        $("#response").html("Please add barcode size!!");
        return;
    }

    $("#response").html("Barcode Generation in progress. Please wait!!");

  
    $.ajax({

        url: 'getBarcodeParamsLogged.htm',
        async:false,
        data: ({user:userName,userAccessLevel:userAccessLevel,ipAddress:ipAddress,barcodeAction:barcodeAction,webSiteAddress:webSiteAddress,message:message,contactName:contactName,contactPhone:contactPhone,contactEmail:contactEmail,sms:sms,call:call,barcodeType:barcodeType,barcodeLength:barcodeLengths.toString(),barcodeWidth:barcodeWidths.toString(),imageType:imageType,errorCorrection:errorCorrection,title:title,smsmessage:smsmessage}),



        success: function(data) {
            var splitArray= data.split("*");
            for (x in splitArray){
                var img=splitArray[x];
                if(img!=""){
                    $("#response").html("");
                    
                    document.getElementById("imageTable").deleteRow(1);
                    
                    $("#imageTable > tbody ").append("<tr><td>"+img+"</td></tr>");
                             
                }
                if(privilegeArray[4]!="0"){
                    //alert("hhhh");
                    $("#getZip").removeAttr('disabled');
                }
                
               
            }
        }

    });

}

function startOverLogged(){

    $("#bcAction").val(1);
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

}
    </script>
