/**
 * 
 */
  // Line color
  LC_CAM = "#5B9BD5";
  // Cấu hình biểu đồ 
  var camConfig = {
    title: '',
    canvasId: 'resultGraph',
    canvasWidth: $("#parentResultGraph").width(),
    canvasHeight: 300,
    minX: -17,
    minY: -16,
    maxX: 21,
    maxY: 21,
    unitsPerTickX: 5,
    unitsPerTickY: 5
  };


 var camGraph = new Graph(camConfig);
 
 // Set background
 $('#resultGraph').css('background-color', 'rgba(158, 167, 184, 0.2)');
 
 function drawCamGraph(camDataX, camDataY, axisData) {
   // Vẽ lại hệ trục tọa độ
   camGraph.clear();
   camConfig.minX = axisData.minX;
   camConfig.maxX = axisData.maxX;
   camConfig.minY = axisData.minY;
   camConfig.maxY = axisData.maxY;
   camConfig.unitsPerTickX = axisData.unitsPerTickX;
   camConfig.unitsPerTickY = axisData.unitsPerTickY;

   // TODO: Cần dùng lại đối tuợng camGraph
   camGraph = new Graph(camConfig);

   drawGraphXY(camGraph, camConfig, camDataX, camDataY, LC_CAM, 2);
 }
