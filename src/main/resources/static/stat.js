//line
var COLORS = [
    '#7FDBFF',
    '#39CCCC',
    '#2ECC40',
    '#FFDC00',
    '#FF851B',
    '#FF4136',
    '#F012BE',
    '#DDDDDD'
];

function getUrl() {
    var man = $("#filterByMan").val();
    var status = $("#filterByTaskStatus").val();
    var path = $("#path").val();

    return {
	    man: man,
	    status: status,
	    path: path
    }
}
