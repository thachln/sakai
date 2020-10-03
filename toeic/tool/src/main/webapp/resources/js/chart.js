
function drawScoreChart(data) {
	var dataResult = [];
	
	for (var i = 0; i < 7; i++) {
		let part = "part" + (i + 1);
		let res = 0;
		
		if (data.correctParts[i] && lengthPartAll[part]) {
			res = parseInt((data.correctParts[i] / lengthPartAll[part]) * 100);
		}
		
		dataResult.push(res);
	}

    //Get the context of the Chart canvas element we want to select
	var chart = $("#radar-chart");

	// Chart Options
	var chartOptions = {
		responsive : true,
		maintainAspectRatio : false,
		responsiveAnimationDuration : 500,
		legend : {
			position : 'top',
		},
		title : {
			display : true,
			text : 'Score Each Part'
		},
		scale : {
			reverse : false,
			ticks : {
				beginAtZero : true,
				max: 100
			}
		}
	};

	// Chart Data
	var chartData = {
		labels : [ "Part 1", "Part 2", "Part 3", "Part 4", "Part 5", "Part 6", "Part 7" ],
		datasets : [ {
			label: "Score Range(%)",
			backgroundColor : "rgba(29,233,182,.6)",
			borderColor : "transparent",
			pointBorderColor : "#FFF",
			pointBackgroundColor : "rgba(29,233,182,.6)",
			pointBorderWidth : 2,
			pointHoverBorderWidth : 2,
			pointRadius : 4,
			data : dataResult,
		} ]
	};

	var config = {
		type : 'radar',

		// Chart Options
		options : chartOptions,

		data : chartData
	};

	// Create the chart
	var polarChart = new Chart(chart, config);
}
