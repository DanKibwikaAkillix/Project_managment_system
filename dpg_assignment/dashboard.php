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

$userName = '';
try {
    $stmt = $pdo->prepare("CALL GetUserNameByEmail(:userEmail)");
    $stmt->bindParam(':userEmail', $_SESSION['email'], PDO::PARAM_STR);
    $stmt->execute();
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($user) {
        $userName = htmlspecialchars($user['userName']);
    } else {
        $userName = "User"; 
    }
} catch (PDOException $e) {
    die("Error fetching user name: " . $e->getMessage());
}


$totalProjects = 0;
$ongoingProjects = 0;
$completedProjects = 0;

try {
    $stmt = $pdo->prepare("CALL CountProjectsByUserEmail(:userEmail)");
    $stmt->bindParam(':userEmail', $_SESSION['email'], PDO::PARAM_STR);
    $stmt->execute();
    $projects = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($projects) {
        $totalProjects = (int)$projects['totalProjects'];
        $ongoingProjects = (int)$projects['ongoingProjects'];
        $completedProjects = (int)$projects['completedProjects'];
    }
} catch (PDOException $e) {
    die("Error fetching project data: " . $e->getMessage());
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Management Dashboard</title>
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
            color: white;
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
            cursor: pointer;
        }

        .sidebar ul li:hover {
            background-color: #3a3f44;
        }

        .sidebar ul li a {
            text-decoration: none;
            color: white;
            display: flex;
            align-items: center;
        }

        .sidebar ul li a i {
            margin-right: 10px;
        }

    
        .main-content {
            margin-left: 250px;
            padding: 20px;
        }

        .navbar {
            display: flex;
            justify-content: space-between;
            background-color: #ffffff;
            padding: 15px;
            border-bottom: 1px solid #ddd;
        }

        .navbar h1 {
            margin: 0;
        }

        .navbar .user-info {
            display: flex;
            align-items: center;
        }

        .navbar .user-info i {
            margin-right: 5px;
        }

        .content {
            margin-top: 20px;
        }

        .card {
            background-color: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .card h3 {
            margin: 0 0 10px;
        }

        .card p {
            margin: 0;
        }

    
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 3px;
            text-decoration: none;
            cursor: pointer;
        }

        .btn:hover {
            background-color: #218838;
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
        <div class="navbar">
            <h1>Dashboard</h1>
            <div class="user-info">
                <i class="fas fa-user"></i>
                
                <span>Welcome, <?php echo $userName; ?></span>
            </div>
        </div>

        <div class="content">
            <div class="card">
                <h3>Project Overview</h3>
                <p style="margin-left:80%; font-size:36px;">Total Projects: <?php echo $totalProjects; ?></p>
                <p style="margin-left:80%;">Ongoing: <?php echo $ongoingProjects; ?> | Completed: <?php echo $completedProjects; ?></p>
                <br>
                <a href="project.php" class="btn">View Projects</a>
            </div>

            <div class="card">
                <h3>Assigned Tasks</h3>
                <p>You have 3 pending tasks.</p>
                <br>
                <a href="#" class="btn">View Tasks</a>
            </div>

            <div class="card">
                <h3>Recent Comments</h3>
                <p>No new comments.</p>
            </div>
        </div>
    </div>

</body>
</html>
