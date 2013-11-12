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
		}
	});
});
