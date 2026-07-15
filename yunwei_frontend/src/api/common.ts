import request from "@/utils/request";

/**
 * 上传菜品图片，返回 OSS 图片地址
 */
export function uploadDishImage(file: File) {
    const formData = new FormData()
    formData.append("file",file)

    return request.post<string,string>('/admin/common/upload',formData,{
        headers:{
            'Content-Type':'multipart/form-data'
        }
    })
}
