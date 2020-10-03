/**
 * 
 */
    // Line color
    LC_ACCELERATION = "#5B9BD5";
    /**
     * Cấu hình biểu đồ gia tốc
     */
    var accelerationConfig = {
        title: '', // Gia tốc
        canvasId: 'giaToc',
        canvasWidth: $("#parentGiaToc").width(),
        canvasHeight: 300,
        minX: -40,
        minY: -66,
        maxX: 400,
        maxY: 65,
        unitsPerTickX: 100,
        unitsPerTickY: 20
    };

    var accelerationGraph = new Graph(accelerationConfig);
    function drawAccelerationGraph(accelerationData, axisData) {
        var len = accelerationData.length;

        accelerationGraph.clear();
        accelerationConfig.minX = axisData.minX;
        accelerationConfig.maxX = axisData.maxX;
        accelerationConfig.minY = axisData.minY;
        accelerationConfig.maxY = axisData.maxY;
        accelerationConfig.unitPerTickX = axisData.unitsPerTickX;
        accelerationConfig.unitPerTickY = axisData.unitsPerTickY;

        console.log("Draw acceleration...");

        accelerationGraph = new Graph(accelerationConfig);

        drawGraph(accelerationGraph, accelerationConfig, function (x) {
            var index = parseInt(x);
            if ((0 <= index) || (index <= len)) {
                return accelerationData[index];
            }
        }, LC_ACCELERATION, 2);
    }