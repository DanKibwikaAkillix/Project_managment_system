<?php
session_start(); 

$host = 'localhost';
$dbname = 'PROJECT_MANAGEMENT_SYSTEM';
$username = 'root';
$password = '';

try {
    $conn = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}


if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $projectName = $_POST['projectName'];
    $projectDescription = $_POST['projectDescription'];
    $startDate = $_POST['startDate'];
    $endDate = $_POST['endDate'];
    $projectStatus = $_POST['projectStatus'];
    $userEmail = $_SESSION['email'] ?? ''; 


    $sql = "INSERT INTO Project (projectName, projectDescription, startDate, endDate, projectStatus, email) 
            VALUES (:projectName, :projectDescription, :startDate, :endDate, :projectStatus, :email)";

    $stmt = $conn->prepare($sql);
    $stmt->execute([
        'projectName' => $projectName,
        'projectDescription' => $projectDescription,
        'startDate' => $startDate,
        'endDate' => $endDate,
        'projectStatus' => $projectStatus,
        'email' => $userEmail, 
    ]);
}

$projects = [];
if (isset($_SESSION['email'])) {
    $userEmail = $_SESSION['email'];
    $stmt = $conn->prepare("SELECT * FROM Project WHERE email = :email");
    $stmt->execute(['email' => $userEmail]);
    $projects = $stmt->fetchAll(PDO::FETCH_ASSOC);
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Dashboard</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }

        .sidebar {
            width: 250px;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            background-color: #24292e;
            color: #fff;
            overflow-y: auto;
        }

        .sidebar h2 {
            text-align: center;
            padding: 15px 0;
            background-color: #2c3e50;
            margin: 0;
        }

        .sidebar ul {
            list-style-type: none;
            padding: 0;
        }

        .sidebar ul li {
            padding: 15px;
        }

        .sidebar ul li a {
            text-decoration: none;
            color: white;
            display: flex;
            align-items: center;
        }

        .sidebar ul li:hover {
            background-color: #3a3f44;
        }

        .main-content {
            margin-left: 250px;
            padding: 20px;
        }

        .card {
            background-color: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .card h3 {
            margin-bottom: 20px;
            font-size: 24px;
            border-bottom: 2px solid #28a745;
            padding-bottom: 10px;
        }

        .card form {
            display: grid;
            gap: 15px;
        }

        .form-group {
            display: flex;
            align-items: center;
            border: 1px solid #ccc;
            border-radius: 4px;
            padding: 10px;
        }

        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 100%;
            border: none;
            outline: none;
            font-size: 14px;
            padding-left: 10px;
        }

        .form-group input::placeholder,
        .form-group textarea::placeholder {
            color: #aaa;
        }

        .form-group i {
            margin-right: 10px;
            color: #28a745;
        }

        .card form button {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 12px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s;
        }

        .card form button:hover {
            background-color: #218838;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        .icon {
            color: #28a745;
            margin-right: 8px;
        }
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
        <div class="card">
            <h3><i class="fas fa-plus-circle"></i> Add New Project</h3>
            <form method="POST">
                <div class="form-group">
                    <i class="fas fa-folder"></i>
                    <input type="text" name="projectName" placeholder="Project Name" required>
                </div>
                <div class="form-group">
                    <i class="fas fa-pencil-alt"></i>
                    <textarea name="projectDescription" placeholder="Project Description" rows="3" required></textarea>
                </div>
                <div class="form-group">
                    <i class="fas fa-calendar-alt"></i>
                    <input type="date" name="startDate" required>
                </div>
                <div class="form-group">
                    <i class="fas fa-calendar-check"></i>
                    <input type="date" name="endDate" required>
                </div>
                <div class="form-group">
                    <i class="fas fa-flag"></i>
                    <input type="text" name="projectStatus" placeholder="Status" required>
                </div>
                <button type="submit">Add Project</button>
            </form>
        </div>

        <div class="card">
            <h3><i class="fas fa-list"></i> Project List</h3>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th>Status</th>
                    <th>User ID</th>
                    <th>Finding ID</th>
                </tr>
                <?php foreach ($projects as $project): ?>
                    <tr>
                        <td><?= htmlspecialchars($project['projectID']) ?></td>
                        <td><?= htmlspecialchars($project['projectName']) ?></td>
                        <td><?= htmlspecialchars($project['projectDescription']) ?></td>
                        <td><?= htmlspecialchars($project['startDate']) ?></td>
                        <td><?= htmlspecialchars($project['endDate']) ?></td>
                        <td><?= htmlspecialchars($project['projectStatus']) ?></td>
                        <td><?= htmlspecialchars($project['UserID']) ?></td>
                        <td><?= htmlspecialchars($project['findingID']) ?></td>
                    </tr>
                <?php endforeach; ?>
            </table>
        </div>
    </div>
</body>
</html>
