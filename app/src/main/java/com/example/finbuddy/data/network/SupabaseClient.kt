package com.example.finbuddy.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://pcezsvqkqinrakvxwrhp.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBjZXpzdnFrcWlucmFrdnh3cmhwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk1NTU0NzksImV4cCI6MjA5NTEzMTQ3OX0.l3AHE2haLuBnYX7J7YLxa1fzHfS0wwTgHjwigRCmqtY"
    ) {
        install(Auth)
        install(Postgrest)
    }
}
