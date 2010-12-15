function addError(message, msg) {
	if(message == "") {
		return msg;
	}
	else {
		return message += "<br>" + msg;
	}
}
$(document).ready(function() {
	reset = function() {
		$('.cb_').each(function() {
			if($(this).is(':checked')) {
				doCheck(this, $(this).attr('party'));
			}
		});
	};

	doCheck = function(curSelector, party) {
		if($('#cb_all').is(':checked') && !$(this).is(':checked')) {
			$('#cb_all').attr('checked',false);
		}
		
		$('.senator').each(function(index) {
			var parties = $($('.party').get(index)).html();

			var check = $(this).children("INPUT[type='checkbox']");

			var re = new RegExp('(\\(|\\- )' + party
					+ '(\\)| \\-)');
			if (parties.match(re)) {
				$(check).attr('checked',
						$(curSelector).is(':checked'));
			}
		});
	};

	clearAll = function() {
		$("INPUT[type='checkbox']").attr('checked', false);
	};

	$('#cb_all').change(
			function() {
				$("INPUT[type='checkbox']").attr('checked',
					$('#cb_all').is(':checked'));
			});

	$('.cb_').change(function() {
		doCheck(this, $(this).attr('party'));
		reset();
	});
	
	$('.sen_').change(function() {
		if($('#cb_all').is(':checked') && !$(this).is(':checked')) {
			$('#cb_all').attr('checked',false);
		}
	});

	$('#process').click(function(event) {
		message = "";
		e1 = document.forms.senators.email1.value;
		e2 = document.forms.senators.email2.value;
		fn = document.forms.senators.firstname.value;
		ln = document.forms.senators.lastname.value;
		
		if(!fn){
			message = addError(message, "Enter your first name");
		}
		if(!ln) {
			message = addError(message, "Enter your last name");
		}
		if(e1 == null || e2 == null || e1 != e2) {
			message = addError(message, "Your email addresses must match!");
		}
		else {
			if(!e1.match(/.*?@.*?\..*?/)) {
				message = addError(message, "Enter a valid email address");
			}
		}
		
		if(message != "") {
			$("#error").html(message);
			$("#error").css({'display' : 'inherit'});
			$('html,body').animate({
				scrollTop:$("#error").offset().top}, 500);
			return false;
		}
		else {
			return true;
		}
	});
});