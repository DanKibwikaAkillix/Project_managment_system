<?php
session_start(); // Start session

// Database connection
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

// Handle form submissions
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Check which action is being performed
    if (isset($_POST['add'])) {
        // Add a new file
        $stmt = $pdo->prepare("CALL AddFile(:fileName, :action, :fileType, :findingName, :findingType, :description, :filePath, :uploadedBy)");
        $stmt->execute([
            ':fileName' => $_POST['fileName'],
            ':action' => $_POST['action'],
            ':fileType' => $_POST['fileType'],
            ':findingName' => $_POST['findingName'],
            ':findingType' => $_POST['findingType'],
            ':description' => $_POST['description'],
            ':filePath' => $_POST['filePath'],
            ':uploadedBy' => $_SESSION['email'], // Assuming email is used as the username
        ]);
    } elseif (isset($_POST['delete'])) {
        // Delete a file
        $stmt = $pdo->prepare("CALL DeleteFile(:id)");
        $stmt->execute([':id' => $_POST['fileId']]);
    } elseif (isset($_POST['update'])) {
        // Update a file
        $stmt = $pdo->prepare("CALL UpdateFile(:id, :fileName, :action, :fileType, :findingName, :findingType, :description, :filePath, :uploadedBy)");
        $stmt->execute([
            ':id' => $_POST['fileId'],
            ':fileName' => $_POST['fileName'],
            ':action' => $_POST['action'],
            ':fileType' => $_POST['fileType'],
            ':findingName' => $_POST['findingName'],
            ':findingType' => $_POST['findingType'],
            ':description' => $_POST['description'],
            ':filePath' => $_POST['filePath'],
            ':uploadedBy' => $_SESSION['email'],
        ]);
    }
}

// Fetch all files
$stmt = $pdo->prepare("CALL GetAllFiles()");
$stmt->execute();
$files = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Management Dashboard</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* Basic styles */
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }
        .container {
            display: flex;
        }
        .sidebar {
            width: 250px;
            background: #333;
            padding: 20px;
            color: white;
            height: 100vh;
            position: fixed;
            overflow-y: auto; /* Allow scrolling */
        }
        .sidebar h2 {
            color: #fff;
        }
        .sidebar a {
            color: #bbb;
            text-decoration: none;
            display: block;
            padding: 10px;
        }
        .sidebar a:hover {
            background: #575757;
            color: white;
        }
        .content {
            margin-left: 260px; /* Space for sidebar */
            padding: 20px;
            width: calc(100% - 260px);
        }
        .card {
            background-color: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .btn {
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #218838;
        }

        @media (max-width: 768px) {
            .sidebar {
                position: relative;
                width: 100%;
                height: auto;
            }
            .content {
                margin-left: 0;
                width: 100%;
            }
        }
    </style>
</head>
<body>

 <!-- Sidebar -->
 <div class="sidebar">
        <h2>Project Dashboard</h2>
        <ul>
            <li><a href="dashboard.php"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
            <li><a href="project.php"><i class="fas fa-project-diagram"></i> Projects</a></li>
            <li><a href="team.php"><i class="fas fa-users"></i> Teams</a></li>
            <li><a href="#"><i class="fas fa-comments"></i> Comments</a></li>
            <li><a href="#"><i class="fas fa-file-alt"></i> Files</a></li>
            <li><a href="#"><i class="fas fa-flag"></i> Reports</a></li>
            <li><a  href="index.php"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
        </ul>
    </div>

<div class="content">
    <h1>File Management Dashboard</h1>

    <div class="card" id="addFile">
        <h2>Add File</h2>
        <form method="post">
            <input type="text" name="fileName" placeholder="File Name" required><br>
            <input type="text" name="action" placeholder="Action"><br>
            <input type="text" name="fileType" placeholder="File Type"><br>
            <input type="text" name="findingName" placeholder="Finding Name"><br>
            <input type="text" name="findingType" placeholder="Finding Type"><br>
            <textarea name="description" placeholder="Description"></textarea><br>
            <input type="text" name="filePath" placeholder="File Path" required><br>
            <button type="submit" name="add" class="btn">Add File</button>
        </form>
    </div>

    <div class="card" id="allFiles">
        <h2>All Files</h2>
        <table>
            <tr>
                <th>ID</th>
                <th>File Name</th>
                <th>Uploaded By</th>
                <th>Upload Date</th>
                <th>Actions</th>
            </tr>
            <?php foreach ($files as $file): ?>
            <tr>
                <td><?php echo $file['id']; ?></td>
                <td><?php echo htmlspecialchars($file['fileName']); ?></td>
                <td><?php echo htmlspecialchars($file['uploadedBy']); ?></td>
                <td><?php echo $file['uploadDate']; ?></td>
                <td>
                    <form method="post" style="display:inline;">
                        <input type="hidden" name="fileId" value="<?php echo $file['id']; ?>">
                        <button type="submit" name="delete" class="btn">Delete</button>
                    </form>
                    <form method="post" style="display:inline;">
                        <input type="hidden" name="fileId" value="<?php echo $file['id']; ?>">
                        <input type="text" name="fileName" placeholder="New File Name" required>
                        <button type="submit" name="update" class="btn">Update</button>
                    </form>
                </td>
            </tr>
            <?php endforeach; ?>
        </table>
    </div>
</div>

</body>
</html>
