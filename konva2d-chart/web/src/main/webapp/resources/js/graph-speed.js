/**
 * 
 */
    // Line color
    LC_SPEED = "#5B9BD5";
    
    /**
     * Cấu hình  biểu đồ vận tốc
     */
    var speedConfig = {
        title: '', // Vận tốc
        canvasId: 'vanToc',
        canvasWidth: $("#parentVanToc").width(),
        canvasHeight: 300,
        minX: -40,
        minY: -11,
        maxX: 400,
        maxY: 11,
        unitsPerTickX: 100,
        unitsPerTickY: 5
    };

    var speedGraph = new Graph(speedConfig);
    function drawSpeedGraph(speedData, axisData) {
        var len = speedData.length;

        speedGraph.clear();
        speedConfig.minX = axisData.minX;
        speedConfig.maxX = axisData.maxX;
        speedConfig.minY = axisData.minY;
        speedConfig.maxY = axisData.maxY;
        speedConfig.unitPerTickX = axisData.unitsPerTickX;
        speedConfig.unitPerTickY = axisData.unitsPerTickY;

        speedGraph = new Graph(speedConfig);

        drawGraph(speedGraph, speedConfig, function (x) {
            //console.log("drawSpeedGraph");
            var index = parseInt(x);
            if ((0 <= index) || (index <= len)) {
                return speedData[index];
            }
        }, LC_SPEED, 2);
    }