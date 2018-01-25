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

$(document).ready(function() {
    var ctx = document.getElementById("myChart").getContext('2d');
    var url = getUrl();
    $.get(url.path + ".json?action=getPercentageStat", url, function(data) {
	// And for a doughnut chart
	var myDoughnutChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
		datasets: [{
                    data: data.percentages,
		    backgroundColor: COLORS.slice(0, data.percentages.length)
		}],
		labels: data.users
            },
            options: {}
	});
    });

});
