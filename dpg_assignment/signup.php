<?php
session_start(); // Start the session

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

$errorMessage = '';
$successMessage = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userName = trim($_POST['userName']);
    $emailAddress = trim($_POST['emailAddress']);
    $userPassword = password_hash(trim($_POST['password']), PASSWORD_DEFAULT);
    $roleID = (int)$_POST['roleID'];
    $firstName = trim($_POST['firstName']);
    $lastName = trim($_POST['lastName']);

    try {
        // Call the stored procedure to register the user
        $stmt = $pdo->prepare("CALL RegisterUser(:userName, :emailAddress, :password, :roleID, :firstName, :lastName)");
        $stmt->execute([
            'userName' => $userName,
            'emailAddress' => $emailAddress,
            'password' => $userPassword,
            'roleID' => $roleID,
            'firstName' => $firstName,
            'lastName' => $lastName
        ]);
        $successMessage = "Registration successful! You can now login.";
    } catch (PDOException $e) {
        if ($e->getCode() == 23000) { // Duplicate entry error
            $errorMessage = "Email address is already registered.";
        } else {
            $errorMessage = "Registration failed: " . $e->getMessage();
        }
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Signup</title>
    <link rel="stylesheet" href="styles.css">
    <style>
        body {
            background: linear-gradient(to right, #6a11cb, #2575fc);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 0;
            font-family: 'Arial', sans-serif;
        }
        .signup-container {
            width: 100%;
            max-width: 500px;
            padding: 20px;
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .signup-container h2 {
            margin-bottom: 10px;
            color: #333;
        }
        .input-group {
            margin-bottom: 15px;
            text-align: left;
        }
        .input-group label {
            display: block;
            margin-bottom: 5px;
            color: #333;
        }
        .input-group input {
            width: 96%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 16px;
        }
        .signup-btn {
            width: 100%;
            padding: 12px;
            background-color: #6a11cb;
            border: none;
            border-radius: 5px;
            color: white;
            font-size: 18px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .signup-btn:hover {
            background-color: #2575fc;
        }
        .error-message, .success-message {
            margin-bottom: 10px;
        }
        .error-message {
            color: red;
        }
        .success-message {
            color: green;
        }
    </style>
</head>
<body>
    <div class="signup-container">
        <h2>Create an Account</h2>
        <?php if (!empty($errorMessage)) : ?>
            <p class="error-message"><?php echo $errorMessage; ?></p>
        <?php endif; ?>
        <?php if (!empty($successMessage)) : ?>
            <p class="success-message"><?php echo $successMessage; ?></p>
        <?php endif; ?>
        <form method="POST" action="signup.php" autocomplete="off">
            <div class="input-group">
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" required>
            </div>
            <div class="input-group">
                <label for="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName" required>
            </div>
            <div class="input-group">
                <label for="userName">Username</label>
                <input type="text" id="userName" name="userName" required>
            </div>
            <div class="input-group">
                <label for="emailAddress">Email</label>
                <input type="email" id="emailAddress" name="emailAddress" required>
            </div>
            <div class="input-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="input-group">
                <label for="roleID">Role ID</label>
                <input type="number" id="roleID" name="roleID" required>
            </div>
            <button type="submit" class="signup-btn">Signup</button>
            <p class="register-link">Already have an account? <a href="index.php">Sign in</a></p>
        </form>
    </div>
</body>
</html>
