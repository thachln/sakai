/**
 * 
 */
    // Line color
    LC_TRANSPOSE = "#5B9BD5";
    // Cấu hình biểu đồ chuyển vị
    var transposeConfig = {
        title: '', // Chuyển vị
        canvasId: 'chuyenVi',
        canvasWidth: $("#parentChuyenVi").width(),
        canvasHeight: 300,
        minX: -100,
        minY: -1,
        maxX: 500,
        maxY: 3,
        unitsPerTickX: 100,
        unitsPerTickY: 0.5
    };

    /**
     * Vẽ biểu đồ chuyển vị.
     * Input
     * canvasId: refer geo-arg-ex.jsp
     * parentChuyenVi: id of div contains the canvas. It is used to determine width of canvas
     */
    var transposeGraph = new Graph(transposeConfig);

    function drawTransposeGraph(transposeData, axisData) {
        var len = transposeData.length;

        // Vẽ lại hệ trục tọa độ
        transposeGraph.clear();
        transposeConfig.minX = axisData.minX;
        transposeConfig.maxX = axisData.maxX;
        transposeConfig.minY = axisData.minY;
        transposeConfig.maxY = axisData.maxY;
        transposeConfig.unitsPerTickX = axisData.unitsPerTickX;
        transposeConfig.unitsPerTickY = axisData.unitsPerTickY;

        // TODO: Cần dùng lại đối tuợng transposeGraph
        transposeGraph = new Graph(transposeConfig);

        drawGraph(transposeGraph, transposeConfig, function (x) {
            // console.log("transposeData: " + transposeData);
            var index = parseInt(x);
            if ((0 <= index) && (index < len)) {
                return transposeData[index];
            }
        }, LC_TRANSPOSE, 2);
    }