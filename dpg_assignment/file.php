<?php
session_start();

if (!isset($_SESSION['email'])) {
    header('Location: login.php');
    exit;
}


$host = 'localhost';
$dbname = 'PROJECT_MANAGEMENT_SYSTEM';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}


if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_FILES['uploadedFile'])) {
    $fileName = $_FILES['uploadedFile']['name'];
    $fileTmpPath = $_FILES['uploadedFile']['tmp_name'];
    $fileType = $_FILES['uploadedFile']['type'];
    $uploadDate = date('Y-m-d H:i:s');
    $uploadedBy = $_SESSION['email'];

    if (!is_dir('File_folder')) mkdir('File_folder', 0777, true);
    $destination = "File_folder/" . $fileName;

    if (move_uploaded_file($fileTmpPath, $destination)) {
        try {
            $stmt = $pdo->prepare("CALL InsertFileRecord(:fileName, :fileType, :filePath, :uploadedBy, :uploadDate)");
            $stmt->execute([
                ':fileName' => $fileName,
                ':fileType' => $fileType,
                ':filePath' => $destination,
                ':uploadedBy' => $uploadedBy,
                ':uploadDate' => $uploadDate
            ]);
            $successMessage = "File uploaded successfully!";
        } catch (PDOException $e) {
            $errorMessage = "Error inserting file: " . $e->getMessage();
        }
    } else {
        $errorMessage = "Failed to upload the file.";
    }
}


if (isset($_GET['delete'])) {
    $id = $_GET['delete'];
    try {
        $stmt = $pdo->prepare("CALL DeleteFile(:id)");
        $stmt->execute([':id' => $id]);
        header('Location: file.php');
        exit;
    } catch (PDOException $e) {
        die("Error deleting file: " . $e->getMessage());
    }
}


try {
    $stmt = $pdo->prepare("CALL GetAllFiles()");
    $stmt->execute();
    $files = $stmt->fetchAll(PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    die("Error fetching files: " . $e->getMessage());
}
?>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Management</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body { margin: 0; font-family: Arial, sans-serif; background-color: #eef2f7; }
        .sidebar { width: 250px; height: 100vh; position: fixed; background-color: #1a1a2e; color: white; }
        .sidebar h2 { text-align: center; padding: 20px 0; background-color: #0f3460; margin: 0; }
        .sidebar ul { list-style-type: none; padding: 0; }
        .sidebar ul li { padding: 15px; cursor: pointer; }
        .sidebar ul li:hover { background-color: #162447; }
        .sidebar ul li a { text-decoration: none; color: white; display: flex; align-items: center; }
        .sidebar ul li a i { margin-right: 10px; }
        .main-content { margin-left: 250px; padding: 30px; }
        .card { background-color: #fff; padding: 20px; border-radius: 8px; margin-bottom: 15px; }
        .btn { padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer; }
        .btn:hover { background-color: #218838; }
        .file-list { margin-top: 20px; }
        .file-item { display: flex; justify-content: space-between; align-items: center; padding: 15px; background-color: #fff; margin-bottom: 10px; border-radius: 5px; }
    </style>
</head>
<body>

<div class="sidebar">
    <h2>Project Dashboard</h2>
    <ul>
            <li><a href="dashboard.php"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
            <li><a href="project.php"><i class="fas fa-project-diagram"></i> Projects</a></li>
            <li><a href="team.php"><i class="fas fa-users"></i> Teams</a></li>
            <li><a href="#"><i class="fas fa-comments"></i> Comments</a></li>
            <li><a href="file.php"><i class="fas fa-file-alt"></i> Files</a></li>
            <li><a href="#"><i class="fas fa-flag"></i> Reports</a></li>
            <li><a href="index.php"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
        </ul>
</div>

<div class="main-content">
    <h1>File Management</h1>

    <form action="file.php" method="POST" enctype="multipart/form-data">
        <input type="file" name="uploadedFile" required>
        <button type="submit" class="btn">Upload File</button>
    </form>

    <div class="file-list">
        <h2>Uploaded Files</h2>
        <?php if (empty($files)): ?>
            <p>No files uploaded yet.</p>
        <?php else: ?>
            <?php foreach ($files as $file): ?>
                <div class="file-item">
                    <span><?php echo htmlspecialchars($file['fileName']); ?></span>
                    <div>
                        <a href="<?php echo $file['filePath']; ?>" target="_blank">Open</a>
                        <a href="?delete=<?php echo $file['id']; ?>" onclick="return confirm('Are you sure?')">Delete</a>
                    </div>
                </div>
            <?php endforeach; ?>
        <?php endif; ?>
    </div>
</div>

</body>
</html>
