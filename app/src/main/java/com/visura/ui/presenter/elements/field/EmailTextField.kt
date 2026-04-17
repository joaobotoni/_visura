package com.visura.ui.presenter.elements.field

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.visura.R
import com.visura.ui.presenter.theme.IconSize

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    StandardTextField(
        value = value,
        placeholder = stringResource(R.string.field_placeholder_email),
        label = stringResource(R.string.field_label_email),
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier.size(IconSize.Default),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier.fillMaxWidth()
    )
}
