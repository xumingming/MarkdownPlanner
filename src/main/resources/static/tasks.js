
$(document).ready(function() {
    if(window.location.hash) {
	var alink = "a[href='" + window.location.hash + "']";
	$(alink).click()
    }
    
    $('#tabs .nav-tabs a').click(function (e) {
	e.preventDefault();
	window.location.hash = $(this).attr('href');	
	$(this).tab('show');
    });
})



