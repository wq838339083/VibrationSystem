<?php
// 设置响应头，允许跨域请求
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// 数据库配置
$db_host = 'localhost';
$db_user = 'your_db_username'; // 需要替换为实际的数据库用户名
$db_pass = 'your_db_password'; // 需要替换为实际的数据库密码
$db_name = 'vibration_app';

// 连接数据库
$conn = new mysqli($db_host, $db_user, $db_pass, $db_name);

// 检查连接
if ($conn->connect_error) {
    die(json_encode(['status' => 'error', 'message' => '数据库连接失败: ' . $conn->connect_error]));
}

// 设置字符集
$conn->set_charset('utf8');

// 处理请求
$action = isset($_GET['action']) ? $_GET['action'] : '';

switch ($action) {
    case 'send':
        // 处理发送震动模式的请求
        sendVibrationPattern($conn);
        break;
    
    case 'receive':
        // 处理接收震动模式的请求
        receiveVibrationPattern($conn);
        break;
    
    default:
        echo json_encode(['status' => 'error', 'message' => '无效的操作']);
        break;
}

/**
 * 发送震动模式到数据库
 */
function sendVibrationPattern($conn) {
    // 检查是否为POST请求
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        echo json_encode(['status' => 'error', 'message' => '只接受POST请求']);
        return;
    }
    
    // 获取POST数据
    $data = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($data['pattern']) || empty($data['pattern'])) {
        echo json_encode(['status' => 'error', 'message' => '缺少震动模式参数']);
        return;
    }
    
    $pattern = $data['pattern'];
    $timestamp = date('Y-m-d H:i:s');
    
    // 将震动模式保存到数据库
    $stmt = $conn->prepare("INSERT INTO vibration_patterns (pattern, created_at) VALUES (?, ?)");
    $stmt->bind_param("ss", $pattern, $timestamp);
    
    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => '震动模式已发送']);
    } else {
        echo json_encode(['status' => 'error', 'message' => '发送震动模式失败: ' . $stmt->error]);
    }
    
    $stmt->close();
}

/**
 * 从数据库接收最新的震动模式
 */
function receiveVibrationPattern($conn) {
    // 获取最新的震动模式
    $result = $conn->query("SELECT id, pattern, created_at FROM vibration_patterns ORDER BY id DESC LIMIT 1");
    
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        echo json_encode(['status' => 'success', 'data' => $row]);
    } else {
        echo json_encode(['status' => 'error', 'message' => '没有可用的震动模式']);
    }
}

// 关闭数据库连接
$conn->close();
?>