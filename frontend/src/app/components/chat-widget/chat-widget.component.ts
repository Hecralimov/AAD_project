import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AssistantService } from '../../services/assistant.service';
import { ToastService } from '../../services/toast.service';
import { AssistantAction, AssistantChatResponse } from '../../models/assistant.model';
import { Product } from '../../services/product.service';
import { ChatVisualizationComponent } from '../chat-visualization/chat-visualization.component';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  actions?: AssistantAction[];
  referencedProducts?: Product[];
  visualizationCode?: string;
}

@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [CommonModule, FormsModule, ChatVisualizationComponent],
  templateUrl: './chat-widget.component.html',
  styleUrl: './chat-widget.component.css'
})
export class ChatWidgetComponent {
  private assistantService = inject(AssistantService);
  private toastService = inject(ToastService);
  
  isOpen = signal(false);
  isLoading = signal(false);
  messages = signal<ChatMessage[]>([]);
  userInput = signal('');
  conversationId = signal<string | undefined>(undefined);
  
  toggleWidget() {
    this.isOpen.update(v => !v);
  }
  
  sendMessage() {
    const input = this.userInput().trim();
    if (!input) return;
    
    this.messages.update(msgs => [...msgs, { role: 'user', content: input }]);
    this.userInput.set('');
    this.isLoading.set(true);
    
    this.assistantService.sendMessage(input, this.conversationId()).subscribe({
      next: (response: AssistantChatResponse) => {
        this.conversationId.set(response.conversationId);
        this.messages.update(msgs => [
          ...msgs,
          {
            role: 'assistant',
            content: response.answer,
            actions: response.suggestedActions || [],
            referencedProducts: response.referencedData?.products || [],
            visualizationCode: response.visualizationCode
          }
        ]);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Chat error', err);
        this.messages.update(msgs => [
          ...msgs,
          { role: 'assistant', content: 'Sorry, I encountered an error connecting to the AI assistant.' }
        ]);
        this.isLoading.set(false);
      }
    });
  }
  
  applyAction(action: AssistantAction, products?: Product[]) {
    if (!products) return;
    const success = this.assistantService.applySuggestedCartAction(action, products);
    if (success) {
      this.toastService.success('Item added to cart!');
    } else {
      this.toastService.error('Failed to add item to cart.');
    }
  }
}
