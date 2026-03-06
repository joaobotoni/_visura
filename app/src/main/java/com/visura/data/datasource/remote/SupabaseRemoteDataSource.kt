package com.visura.data.datasource.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import com.visura.data.model.Instrument
import javax.inject.Inject

class SupabaseRemoteDataSource @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun getInstruments(): List<Instrument> {
        return client.from("instruments").select().decodeList<Instrument>()
    }

    suspend fun insertInstrument(instrument: Instrument) {
        client.from("instruments").insert(instrument)
    }
}