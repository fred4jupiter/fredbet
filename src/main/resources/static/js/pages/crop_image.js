$(document).ready(function() {
	imageCroppingUpload();
});

function imageCroppingUpload() {
	var $uploadCrop;

	function readFile(input) {
			if (input.files && input.files[0]) {
            var reader = new FileReader();
            
            reader.onload = function (e) {				
            	$uploadCrop.croppie('bind', {
            		url: e.target.result
            	}).then(function(){
            		console.log('jQuery bind complete');
            	});
            }
            reader.readAsDataURL(input.files[0]);
        }
        else {
	        console.log("Sorry - you're browser doesn't support the FileReader API");
	    }
	}

	$('#upload').on('change', function () { readFile(this); });
	
	$uploadCrop = $('#croppie-container').croppie({
		viewport: {
			width: 150,
			height: 150,			
		},
		enableExif: true,
		enableOrientation: true,
		format: 'jpeg'		
	});
	
	$('#crop-image').on('click', function (ev) {
		$uploadCrop.croppie('result', {
			type: 'canvas',
			size: 'viewport',
			enableExif: true,
			enableOrientation: true,
			format: 'jpeg'
		}).then(function (resp) {
			$("#resultImage").attr("src", resp);
			$("#croppedFileBase64").attr("value", resp);
		});
	});
}
