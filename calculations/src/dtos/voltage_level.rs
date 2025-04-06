use serde::Deserialize;

#[derive(Deserialize)]
pub struct CreateDto {
    pub name: String,
}
#[derive(Deserialize)]
pub struct UpdateDto {
    pub name: String,
}