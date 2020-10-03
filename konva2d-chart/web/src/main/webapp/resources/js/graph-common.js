/**
 * 
 */
// constants
    AXIS_COLOR = '#aaa';
    AXIS_FONT = '8pt Calibri';
    AXIS_TICK_SIZE = 10;
    TITLE_FONT_SIZE = 13;
    N_DEC = 1; // số số lẻ của trục tọa độ xy
    GRID_COLOR = '#ddd';
    /**
     * Init casvas for graph.
     * @param config: contains configuration information for graph
     *  
     *  title:
     *  canvasId: 
     *  canvasWidth: 
     *  canvasHeight:
     *  minX: 
     *  minY: 
     *  maxX: 
     *  maxY: 
     *  unitsPerTickX:
     *  unitsPerTickY:
     *  @return Stage of the graph
     */
    function Graph(config) {
        // first we need to create a stage
        var stage = new Konva.Stage({
            container: config.canvasId, // id of container <div>
            width: config.canvasWidth,
            height: config.canvasHeight
        });
        // console.log("graph-common.jsp:config=" + JSON.stringify(config));
        // then create layer
        var layer = new Konva.Layer();

        // Label
        var titleText;

        // Init the configuration for canvas, graph
        var config = initConfig(config);

        // draw x grids
        var xGrids = drawXGrids(config, GRID_COLOR);

        // draw y grids
        var yGrids = drawYGrids(config, GRID_COLOR);

        // draw x axis
        var xAxis = drawXAxis(config, AXIS_TICK_SIZE, AXIS_COLOR, AXIS_FONT);

        // draw y axis
        var yAxis = drawYAxis(config, AXIS_TICK_SIZE, AXIS_COLOR, AXIS_FONT);

        if (config.title !== '') {
            var titleText = new Konva.Text({
                //       x: stage.getWidth() / 2,
                x: 1,
                y: 5,
                text: config.title,
                fontSize: TITLE_FONT_SIZE
            });

            // add label for title
            layer.add(titleText);
        }

        // add xgrids
        layer.add(xGrids);

        // add ygrids
        layer.add(yGrids);

        // add xaxis
        layer.add(xAxis);

        // add xaxis
        layer.add(yAxis);

        // add the layer to the stage
        stage.add(layer);
        return stage;
    }

    function drawGraph(stage, config, equation, color, thickness) {
        console.log("drawGraph.START");
        // Create layer for graph
        var layer = new Konva.Layer();

        // draw graph of equation
        var graph = new Konva.Shape({
            // a Konva.Canvas renderer is passed into the sceneFunc function (drawFunc is deprecated)
            sceneFunc: function (context) {
                console.log("drawGraph.START draw");
                context.save();
                context.save();
                transformContext(config, context);

                context.beginPath();
                context.moveTo(config.minX, equation(config.minX));

                console.log("config.minX=" + config.minX);
                
                var y;
                for (var x = config.minX + config.iteration; x <= config.maxX; x += config.iteration) {
                	y = equation(x);
                	if (y != null) {
                		context.lineTo(x, y);
                	}
                    // console.log("x=" + x + ";y=" + equation(x));
                }

                context.restore();
                context.lineJoin = 'round';
                context.lineWidth = thickness;
                context.strokeStyle = color;
                context.stroke();
                context.restore();
            }
        });
        layer.add(graph);

        // add the layer to the stage
        stage.add(layer);

        return graph;
    }
    
    /**
     * @param stage
     * @param config
     * @param x array of values
     * @param
     * @param 
     */
    function drawGraphXY(stage, config, listX, listY, color, thickness) {
        console.log("drawGraphXY.START");
        // Create layer for graph
        var layer = new Konva.Layer();

        // draw graph of equation
        var graph = new Konva.Shape({
            // a Konva.Canvas renderer is passed into the sceneFunc function (drawFunc is deprecated)
            sceneFunc: function (context) {
                console.log("drawGraphXY.START draw");
                context.save();
                context.save();
                transformContext(config, context);

                context.beginPath();
                context.moveTo(listX[0], listY[0]);

                // console.log("config.minX=" + config.minX);
                var x;
                var y;
                for (var i = 1, len = listX.length; i < len; i++) {
                	x = listX[i];
                	
                	y = listY[i];
                	//console.log("(x,y)=" + "(" + x + "," + y + ")");
                	context.lineTo(x, y);
//                	if (y != null) {
//                		context.lineTo(x, y);
//                	} else {
//                		console.log("y=" + y);
//                	}
                }

                context.restore();
                context.lineJoin = 'round';
                context.lineWidth = thickness;
                context.strokeStyle = color;
                context.stroke();
                context.restore();
            }
        });
        layer.add(graph);

        // add the layer to the stage
        stage.add(layer);

        return graph;
    }
    


    function transformContext(config, context) {
        var myContext = context;

        // move context to center of canvas
        context.translate(config.centerX, config.centerY);

        /*
         * stretch grid to fit the canvas window, and
         * invert the y scale so that that increments
         * as you move upwards
         */
        console.log("config.scaleX=" + config.scaleX + ";config.scaleY" + config.scaleY);
        myContext.scale(config.scaleX, -config.scaleY);
    }
    ;


    /**
     * Update configuration.
     * @input minX, maxX, minY, maxY,
     * @output
     * unitX, unitY, centerX, centerY
     * iteration
     * scaleX
     * scaleY
     */
    function initConfig(config) {
        config.rangeX = config.maxX - config.minX;
        config.rangeY = config.maxY - config.minY;

        config.unitX = config.canvasWidth / config.rangeX;
        config.unitY = (config.canvasHeight - 40) / config.rangeY; // Trừ 40 để vẽ label đơn vị ở đáy trục Y


        config.centerX = Math.round(Math.abs(config.minX / config.rangeX) * config.canvasWidth);
        config.centerY = Math.round(Math.abs(config.maxY / config.rangeY) * config.canvasHeight);

        config.iteration = (config.maxX - config.minX) / 1000;
        config.scaleX = config.canvasWidth / config.rangeX;
        config.scaleY = config.canvasHeight / config.rangeY;

        return config;
    }
    /**
     * 
     */
    function drawXAxis(config, tickSize, axisColor, font) {
        // draw x axis
        var xAxis = new Konva.Shape({
            // a Konva.Canvas renderer is passed into the sceneFunc function (drawFunc is deprecated)
            sceneFunc: function (context) {


                var canvas = context.getCanvas();

                context.beginPath();
                context.moveTo(0, config.centerY);
                context.lineTo(canvas.width, config.centerY);
                context.strokeStyle = axisColor;
                context.lineWidth = 2;
                context.stroke();

                // draw tick marks
                var xPosIncrement = config.unitsPerTickX * config.unitX;
                var xPos, unit;
                context.font = font;
                context.textAlign = 'center';
                context.textBaseline = 'top';

                // draw left tick marks
                
                // Debug
                xPos = config.centerX - xPosIncrement;
                
                unit = -1 * config.unitsPerTickX;
                while (xPos > 0) {
                    context.moveTo(xPos, config.centerY - tickSize / 2);
                    context.lineTo(xPos, config.centerY + tickSize / 2);
                    context.stroke();

                    if (checkInt(unit)) {
                        context.fillText(unit, xPos, config.centerY + tickSize / 2 + 3);
                    } else {
                        context.fillText(unit.toFixed(N_DEC), xPos, config.centerY + tickSize / 2 + 3);
                    }
                    unit -= config.unitsPerTickX;
                    xPos = Math.round(xPos - xPosIncrement);
                }

                // draw right tick marks
                xPos = config.centerX + xPosIncrement;
                unit = config.unitsPerTickX;
                while (xPos < canvas.width) {
                    context.moveTo(xPos, config.centerY - tickSize / 2);
                    context.lineTo(xPos, config.centerY + tickSize / 2);
                    context.stroke();

                    if (checkInt(unit)) {
                        context.fillText(unit, xPos, config.centerY + tickSize / 2 + 3);
                    } else {
                        context.fillText(unit.toFixed(N_DEC), xPos, config.centerY + tickSize / 2 + 3);
                    }
                    unit += config.unitsPerTickX;
                    xPos = Math.round(xPos + xPosIncrement);
                }
            }
        });

        return xAxis;
    }

    function drawYAxis(config, tickSize, axisColor, font) {
        // draw y axis
        var yAxis = new Konva.Shape({
            // a Konva.Canvas renderer is passed into the sceneFunc function (drawFunc is deprecated)
            sceneFunc: function (context) {

                var canvas = context.getCanvas();

                context.beginPath();
                context.moveTo(config.centerX, 0);
                context.lineTo(config.centerX, canvas.height);
                context.strokeStyle = axisColor;
                context.lineWidth = 2;
                context.stroke();

                // draw tick marks
                var yPosIncrement = config.unitsPerTickY * config.unitY;
                var yPos, unit;
                context.font = font;
                context.textAlign = 'right';
                context.textBaseline = 'middle';

                // draw top tick marks
                yPos = config.centerY - yPosIncrement;
                
                
                unit = config.unitsPerTickY;
                console.log("drawYAxis.top.unit=" + unit + ";yPos=" + yPos);
                
                while (yPos > 0) {
                	console.log("drawYAxis.top.unit=" + unit + ";yPos=" + yPos);
                    context.moveTo(config.centerX - tickSize / 2, yPos);
                    context.lineTo(config.centerX + tickSize / 2, yPos);
                    context.stroke();
                    if (checkInt(unit)) {
                        context.fillText(unit, config.centerX - tickSize / 2 - 3, yPos);
                    } else {
                        context.fillText(unit.toFixed(N_DEC), config.centerX - tickSize / 2 - 3, yPos);
                    }
                    unit += config.unitsPerTickY;
                    yPos = Math.round(yPos - yPosIncrement);
                    
                }

                // draw bottom tick marks
                yPos = config.centerY + yPosIncrement;

                unit = -1 * config.unitsPerTickY;
                console.log("drawYAxis.bottom.unit=" + unit + ";yPos=" + yPos + ";canvas.height=" + canvas.height);
                
                while (yPos <= canvas.height) {
                	console.log("drawYAxis.bottom.unit=" + unit + ";yPos=" + yPos);
                	
                    context.moveTo(config.centerX - tickSize / 2, yPos);
                    context.lineTo(config.centerX + tickSize / 2, yPos);
                    context.stroke();

                    if (checkInt(unit)) {
                        context.fillText(unit, config.centerX - tickSize / 2 - 3, yPos);
                    } else {
                        context.fillText(unit.toFixed(N_DEC), config.centerX - tickSize / 2 - 3, yPos);
                    }
                    unit -= config.unitsPerTickY;
                    yPos = Math.round(yPos + yPosIncrement);
                }
            }
        });

        return yAxis;
    }
    /**
     * Draw x lines for grids.
     * @param {type} config uses properties: unitsPerTickX, unitX, centerX
     * @param {type} gridColor color of grid lines
     * @returns {Konva.Shape}
     */
    function drawXGrids(config, gridColor) {
        // draw x grids
        var xGrids = new Konva.Shape({
            // a Konva.Canvas renderer is passed into the sceneFunc function (drawFunc is deprecated)
            sceneFunc: function (context) {


                var canvas = context.getCanvas();

                context.strokeStyle = gridColor;
                context.lineWidth = 1;

                var xPosIncrement = config.unitsPerTickX * config.unitX;
                var xPos, unit;

                // draw left grids lines
                 xPos = config.centerX - xPosIncrement;
                unit = -1 * config.unitsPerTickX;
                while (xPos > 0) {
                    context.moveTo(xPos, 0);
                    context.lineTo(xPos, canvas.height);
                    context.stroke();

                    unit -= config.unitsPerTickX;
                    xPos = Math.round(xPos - xPosIncrement);
                }

                // draw right grids lines
                xPos = config.centerX + xPosIncrement;
                unit = config.unitsPerTickX;
                while (xPos < canvas.width) {
                    context.moveTo(xPos, 0);
                    context.lineTo(xPos, canvas.height);
                    context.stroke();

                    unit += config.unitsPerTickX;
                    xPos = Math.round(xPos + xPosIncrement);
                }
            }
        });

        return xGrids;
    }

    /**
     * Draw y lines for grid.
     * @param {type} config uses properties: unitsPerTickY, unitY, centerY
     * @param {type} gridColor color of grid lines
     * @returns {Konva.Shape}
     */
    function drawYGrids(config, gridColor) {
        // draw y grids
        var yGrids = new Konva.Shape({
            // a Konva.Canvas renderer is passed into the sceneFunc function (drawFunc is deprecated)
            sceneFunc: function (context) {

                var canvas = context.getCanvas();

                context.strokeStyle = gridColor;
                context.lineWidth = 1;

                var yPosIncrement = config.unitsPerTickY * config.unitY;
                var yPos, unit;

                // draw top grids lines
                yPos = config.centerY - yPosIncrement;
                unit = config.unitsPerTickY;
                while (yPos > 0) {
                    context.moveTo(0, yPos);
                    context.lineTo(canvas.width, yPos);
                    context.stroke();

                    unit += config.unitsPerTickY;
                    yPos = Math.round(yPos - yPosIncrement);
                }

                // draw bottom grids lines
                yPos = config.centerY + yPosIncrement;
                unit = -1 * config.unitsPerTickY;
                while (yPos < canvas.height) {
                    context.moveTo(0, yPos);
                    context.lineTo(canvas.width, yPos);
                    context.stroke();

                    unit -= config.unitsPerTickY;
                    yPos = Math.round(yPos + yPosIncrement);
                }
            }
        });

        return yGrids;
    }

    /**
     * Check if number is integer or not.
     * @param {type} unit number
     * @returns {Boolean} true is integer
     */
    function checkInt(unit) {
        return (Number(unit) === unit) && (unit % 1 === 0);
    }