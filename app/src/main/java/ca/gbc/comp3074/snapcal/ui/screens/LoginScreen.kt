package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import ca.gbc.comp3074.snapcal.ui.theme.*
import ca.gbc.comp3074.snapcal.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authVm: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onAdminClick: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var pass     by remember { mutableStateOf("") }
    var pass2    by remember { mutableStateOf("") }
    
    val errorMsg by authVm.error.collectAsState()

    Box(modifier=Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(PinkLight,PeachAccent)))) {
        Column(modifier=Modifier.fillMaxSize().padding(28.dp), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally) {
            Text("🍽️", fontSize=60.sp)
            Text("SnapCal", style=MaterialTheme.typography.headlineLarge, fontWeight=FontWeight.ExtraBold, color=Color.White)
            Text("Smart Meal Planner & Nutrition Tracker", style=MaterialTheme.typography.bodyMedium, color=Color.White.copy(0.85f))
            Spacer(Modifier.height(32.dp))
            Card(modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(24.dp), elevation=CardDefaults.cardElevation(12.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
                    Text(if(isSignUp) "Create Account" else "Welcome Back", style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold)
                    
                    if(isSignUp) OutlinedTextField(value=username,onValueChange={username=it},label={Text("Username")},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(12.dp))
                    OutlinedTextField(value=email,onValueChange={email=it},label={Text("Email")},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(12.dp))
                    OutlinedTextField(value=pass,onValueChange={pass=it},label={Text("Password")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(12.dp))
                    if(isSignUp) OutlinedTextField(value=pass2,onValueChange={pass2=it},label={Text("Confirm Password")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(12.dp))
                    
                    if(errorMsg.isNotEmpty()) Text(errorMsg, color=MaterialTheme.colorScheme.error, style=MaterialTheme.typography.bodySmall)
                    
                    Button(
                        onClick={
                            if(isSignUp) {
                                if (pass == pass2) {
                                    authVm.signup(username, email, pass, onLoginSuccess)
                                }
                            } else {
                                authVm.login(email, pass, onLoginSuccess)
                            }
                        }, 
                        modifier=Modifier.fillMaxWidth(), 
                        shape=RoundedCornerShape(12.dp), 
                        colors=ButtonDefaults.buttonColors(containerColor=PinkPrimary)
                    ) { Text(if(isSignUp) "Sign Up" else "Login", fontWeight=FontWeight.SemiBold) }
                    
                    TextButton(onClick={isSignUp=!isSignUp},modifier=Modifier.fillMaxWidth()) { Text(if(isSignUp) "Already have an account? Login" else "New here? Create account") }
                    
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    
                    // Admin Button for direct access
                    OutlinedButton(
                        onClick = onLoginSuccess,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Direct Admin Access")
                    }
                }
            }
        }
    }
}
