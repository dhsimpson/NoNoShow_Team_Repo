function AjaxCall(){
    var formData = new FormData();

    for(var i=0; i<$('#photo_upload')[0].files.length; i++){
        formData.append('uploadFile', $('#photo_upload')[0].files[i]);
    }
    $.ajax({
        url:'https://1mti06fa1l.execute-api.ap-northeast-2.amazonaws.com/nonoshow/compimgupload',
        type: 'POST',
        data: formData,
        cache: false,
        dataType: 'json',
        processData: false,
        contentType: false,
        success: function(data){console.log(data);alert('성공!!!');},
        error: function(err){alert('실패!!');}
     });
 }