
    // Authenticate user before login when username and password is given
function validateUser(ipAddress){

    var userName=$("#loginName").val();
    var password=$("#loginPassword").val();

    $.ajax({
        url: 'validateUser.htm',
        async: false,
        data: ({ipAddress:ipAddress,userName:userName,password:password}),
        success: function(data) {

            if(data=='-2'){
                alert("UserName and Password cannot be empty!");
            }
            else if(data=='-1'){
                alert("Invalid user!");
            }

                
            else
            {
                document.location.href = 'loggeduserscreen.htm';
            }
        }
    });

}

function logoutUser(){

    // deleteCookie('sessionKey');
    //document.location.href = 'login.htm';
}



