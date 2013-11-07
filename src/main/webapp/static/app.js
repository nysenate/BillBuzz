$(document).ready(function() {
	// Clear the board completely.
	$("INPUT[name='clear']").click(function() {
		$("INPUT[type='checkbox']").attr('checked', false);
	});

	// Select everything!
	var all = $("INPUT[name='all']");
	all.change(function() {
		$("INPUT[type='checkbox']").attr('checked', $(this).is(':checked'));
	});

	// Unselect "all" and any related parties if deselected
	var senators = $("INPUT[name='senators']");
	senators.change(function() {
		if(!$(this).is(':checked')) {
			all.attr('checked',false);
			var classes = this.className.split(" ");
			for (var i = 0; i < classes.length; i++) {
				$("INPUT[value='"+classes[i]+"']").attr('checked', false);
			}
		}
	});

	// Unselect party senators, then reselect all relevant party senators
	var parties = $("INPUT[name='parties']");
	parties.change(function() {
		if(!$(this).is(':checked')) {
			all.attr('checked',false);
		}
		$("INPUT."+$(this).val()).attr('checked', $(this).is(':checked'));
		parties.each(function() {
			if ($(this).is(':checked')) {
				$("INPUT."+$(this).val()).attr('checked', $(this).is(':checked'));
			}
		});
	});
});
