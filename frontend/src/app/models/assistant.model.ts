import { Product } from '../services/product.service';

export interface AssistantChatRequest {
  message: string;
  conversationId?: string;
}

export interface AssistantAction {
  type: string;
  productId: string | number;
  quantity: number;
  label: string;
  confidence: number;
}

export interface AssistantChatResponse {
  conversationId: string;
  answer: string;
  suggestedActions: AssistantAction[];
  referencedData: {
    products: Product[];
    [key: string]: any;
  };
  visualizationCode?: string;
}
