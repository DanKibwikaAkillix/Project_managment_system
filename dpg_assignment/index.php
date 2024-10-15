<?php
session_start(); // Start session

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
    $email = trim($_POST['email']);
    $userPassword = trim($_POST['password']);

    try {
        // Call the stored procedure
        $stmt = $pdo->prepare("CALL GetUserByEmail(:email)");
        $stmt->bindParam(':email', $email);
        $stmt->execute();
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user) {
            if ($user['accountStatus'] !== 'active') {
                $errorMessage = "Account is inactive.";
            } elseif (password_verify($userPassword, $user['password'])) {
                // Start the session and store user information
                $_SESSION['userID'] = $user['userID'];
                $_SESSION['userName'] = $user['userName'];
                $_SESSION['email'] = $user['emailAddress'];

                // Redirect to dashboard.php
                header("Location: dashboard.php");
                exit(); // Make sure to exit after redirecting
            } else {
                $errorMessage = "Invalid password.";
            }
        } else {
            $errorMessage = "User not found.";
        }
    } catch (PDOException $e) {
        $errorMessage = "Error: " . $e->getMessage();
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="styles.css">
    <style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
        font-family: 'Arial', sans-serif;
    }

    body {
        background: linear-gradient(to right, #6a11cb, #2575fc);
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .login-container {
        width: 100%;
        max-width: 400px;
        padding: 20px;
    }

    .login-box {
        background-color: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        text-align: center;
    }

    .login-box h2 {
        margin-bottom: 10px;
        color: #333;
    }

    .login-box p {
        color: #666;
        margin-bottom: 20px;
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
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
        font-size: 16px;
    }

    .options {
        display: flex;
        justify-content: space-between;
        margin-bottom: 20px;
        font-size: 14px;
    }

    .options a {
        color: #2575fc;
        text-decoration: none;
    }

    .login-btn {
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

    .login-btn:hover {
        background-color: #2575fc;
    }

    .register-link {
        margin-top: 15px;
        font-size: 14px;
    }

    .register-link a {
        color: #6a11cb;
        text-decoration: none;
        font-weight: bold;
    }

    .register-link a:hover {
        text-decoration: underline;
    }

    .error-message {
        color: red;
        margin-bottom: 10px;
    }

    .success-message {
        color: green;
        margin-bottom: 10px;
    }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-box">
            <h2>PmS</h2>
            <p>Please login to your account</p>
            <?php if (!empty($errorMessage)) : ?>
                <p class="error-message"><?php echo $errorMessage; ?></p>
            <?php endif; ?>
            <?php if (!empty($successMessage)) : ?>
                <p class="success-message"><?php echo $successMessage; ?></p>
            <?php endif; ?>
            <form method="POST" action="" autocomplete="off">
                <div class="input-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" placeholder="Enter your email" required>
                </div>
                <div class="input-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Enter your password" required>
                </div>
                <div class="options">
                    <label><input type="checkbox" name="remember"> Remember me</label>
                    <a href="#">Forgot Password?</a>
                </div>
                <button type="submit" class="login-btn">Login</button>
            </form>
            <p class="register-link">Don't have an account? <a href="signup.php">Sign up</a></p>
        </div>
    </div>
</body>
</html>
