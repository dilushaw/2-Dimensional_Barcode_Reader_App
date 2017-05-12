 
   $(document).ready(function() {

    startOverDefault();
    $("[name=barCodetype]").filter("[value=1]").attr("checked","checked");



});

function limitText(textboxname, countboxname, limitNum) {

    limitField = document.getElementById(textboxname);
    limitCount = document.getElementById(countboxname);
    if (limitField.value.length > limitNum) {
        limitField.value = limitField.value.substring(0, limitNum);
    } else {
        limitCount.value = limitNum - limitField.value.length;
    }
}


function generateBarcodeDefault(ipAddress){
    $("#response").html("");

     submitBarcodeParamsDefault(ipAddress);
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
                msg="Website URL is not valid.";
            }
            break;
        case "2":
            if(message==""){
                msg= "Please insert a message!!";
            }

           break;
         case "3":
              regex = /^((\d+)|(\+(\d+)))$/;
            // regex = /^\d{11}$/;
            if(contactName==""){
                msg= "Please insert a Contact Name!!";
            }
            else if(contactPhone==""){
                    msg= "Please insert a Phone Number!!";
             }
            else if(contactEmail==""){
                msg= "Please insert an email address!!";
            }

            else if(!contactPhone.match(regex) ){
                msg ="The phone number is incorrect ";

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


function submitBarcodeParamsDefault(ipAddress){

    var barcodeAction;
    var webSiteAddress;
    var message;
    var contactName;
    var contactPhone;
    var contactEmail;
    var sms;
    var smsmessage;
    var call;
    var bcType;
    var barCodeSize;
    var errorCorrection;
    var title;

    barcodeAction=$("#BCaction").val();
    webSiteAddress=$("#websiteAddress").val();
    message=$("#message").val();
    contactName=$("#contactName").val();
    contactPhone=$("#contactPhone").val();
    contactEmail=$("#contactEmail").val();
    sms="77"+$("#sms").val();
    call="77"+$("#call").val();
    bcType=$('input:radio[name=barCodetype]:checked').val();
    barCodeSize=$("#barcodeSize").val();
    errorCorrection=$("#errorCorrection").val();
    title=$("#titleText").val();
    smsmessage=$("#smsmessage").val();

    $("#downloadImage").attr('disabled', 'disabled');

    
    msg =validate(barcodeAction,webSiteAddress,message,contactName,contactPhone,contactEmail,sms,call,smsmessage);

  //  alert(msg);

    if (msg) {
	$("#response").html(msg);
        return;
    }

   

     $("#response").html("Barcode Generation in progress. Please wait!!");

    $.ajax({

        url: 'getBarcodeParamsDefault.htm',
        async:false,
        data: ({ipAddress:ipAddress,barcodeAction:barcodeAction,webSiteAddress:webSiteAddress,message:message,contactName:contactName,contactPhone:contactPhone,contactEmail:contactEmail,sms:sms,call:call,barcodeType:bcType,barCodeSize:barCodeSize,errorCorrection:errorCorrection,title:title,ImageType:"1",smsmessage:smsmessage}),

        success: function(data) {
            $("#response").html("");

           
            $("#image").html(data);

             $("#downloadImage").removeAttr('disabled');
       
        }

    });

  
 }




function changeBarcodeType(){
    
    var barCodeType;
    barCodeType=$('input:radio[name=barCodetype]:checked').val();

    if(barCodeType=="2")
        $("#errorCorrection").attr('disabled', 'disabled');
    else
        $("#errorCorrection").removeAttr('disabled');

}

function tt(){
	alert("test");
}


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

function startOverDefault(){

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
    $("#barcodeSize").val(1);
    $("#errorCorrection").val(1);
    $("#typeQR").prop('checked', true);
    $("#typeDM").prop('checked', false);
    $("#errorCorrection").removeAttr('disabled');
    $("#image").html("<img src=\"images/NoImage.png\" height=\"150\" id=\"barcodeImg\" />");
    $("[name=barCodetype]").filter("[value=1]").attr("checked","checked");
    $("#image").html("<img src='images/NoImage.png' height='150' id='barcodeImg' />");
    $("#downloadImage").attr('disabled', 'disabled');
    
    $("#msgboxcount").val('2000');
    $("#smsmsgboxcount").val('160');

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



/*function showBarcodeImage(){

    alert("start get Image");
       $.ajax({

        url: 'getImageStream.htm',
        async:false,
        data: ({Param1:"barcodeAction:1"}),

        success: function(data) {

            alert(data);
            var result = "";
            for (var i = 0; i < data.length; i++) {
                result += String.fromCharCode(parseInt(data[i], 2));
            }


            $("#image").html(data);

        }

    });






}*/




