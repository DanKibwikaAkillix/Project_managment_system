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
    $memberName = $_POST['memberName'];
    $memberEmail = $_POST['memberEmail'];
    $projectName = $_POST['projectName'];
    $userEmail = $_SESSION['email']; 
 
    $sql = "CALL AddTeamMember(:memberName, :memberEmail, :projectName, :userEmail)";
    $stmt = $conn->prepare($sql);
    
    try {
        $stmt->execute([
            'memberName' => $memberName,
            'memberEmail' => $memberEmail,
            'projectName' => $projectName,
            'userEmail' => $userEmail,
        ]);
        echo "<script>alert('Team member added successfully!');</script>";
    } catch (PDOException $e) {
        echo "<script>alert('Error adding team member: " . $e->getMessage() . "');</script>";
    }
}


$members = $conn->query("SELECT * FROM Team")->fetchAll(PDO::FETCH_ASSOC);


$userEmail = $_SESSION['email']; 
$stmt = $conn->prepare("CALL GetProjectsByUserEmail(:userEmail)");
$stmt->bindParam(':userEmail', $userEmail);
$stmt->execute();

$projects = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Team Dashboard</title>
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
        .form-group select {
            width: 100%;
            border: none;
            outline: none;
            font-size: 14px;
            padding-left: 10px;
        }

        .form-group input::placeholder {
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
            <h3><i class="fas fa-plus-circle"></i> Add New Team Member</h3>
            <form method="POST">
                <div class="form-group">
                    <i class="fas fa-user"></i>
                    <input type="text" name="memberName" placeholder="Member Name" required>
                </div>
                <div class="form-group">
                    <i class="fas fa-envelope"></i>
                    <input type="email" name="memberEmail" placeholder="Member Email" required>
                </div>
                <div class="form-group">
                    <i class="fas fa-project-diagram"></i>
                    <select name="projectName" required>
                        <option value="">Select Project</option>
                        <?php foreach ($projects as $project): ?>
                            <option value="<?= htmlspecialchars($project['projectName']) ?>"><?= htmlspecialchars($project['projectName']) ?></option>
                        <?php endforeach; ?>
                    </select>
                </div>
                <button type="submit">Add Team Member</button>
            </form>
        </div>

        <div class="card">
            <h3><i class="fas fa-list"></i> Team Member List</h3>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                </tr>
                <?php foreach ($members as $member): ?>
                    <tr>
                        <td><?= htmlspecialchars($member['memberID']) ?></td>
                        <td><?= htmlspecialchars($member['memberName']) ?></td>
                        <td><?= htmlspecialchars($member['memberEmail']) ?></td>
                    </tr>
                <?php endforeach; ?>
            </table>
        </div>
    </div>

</body>
</html>
